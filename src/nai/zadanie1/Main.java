package nai.zadanie1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    static int propCount = 0;

    public static void main(String[] args) throws FileNotFoundException {
        int k = Integer.parseInt(args[0]);
        File training = new File(args[1]);
        File test = new File(args[2]);

        List<Observation> trainingObs = loadObsFromFile(training);
        List<Observation> testObs = loadObsFromFile(test);

        int correctGuesses = 0;
        for (Observation o : testObs) {
            List<Observation> nns = findNearestNeighbors(k, o, trainingObs);
            classify(o, nns);
            if (o.getGuessedName().equals(o.getName()))
                correctGuesses++;

            System.out.println(o + " --> " + o.getGuessedName());
            System.out.println("Neighbors: " + nns);
            System.out.println();
        }
        System.out.printf("Accuracy: " + correctGuesses + "/" + testObs.size()
                + " (" + "%.2f" + ")%%%n", (double) correctGuesses / testObs.size() * 100);

        Scanner consoleScanner = new Scanner(System.in);
        System.out.println("[i] Enter additional comma-separated values below,");
        System.out.println("[i] or type \"quit\" to terminate the program.");
        while (true) {
            System.out.print("->");
            String input = consoleScanner.nextLine();
            if (input.equals("quit"))
                break;

            double[] properties = Arrays.stream(input.split(","))
                    .mapToDouble(Double::parseDouble)
                    .toArray();

            Observation o = new Observation(properties);
            List<Observation> nns = findNearestNeighbors(k, o, trainingObs);
            classify(o, nns);

            System.out.println(o + " --> " + o.getGuessedName());
            System.out.println("Neighbors: " + nns);
            System.out.println();
        }
        consoleScanner.close();
    }

    private static void classify(Observation o, List<Observation> neighbors) {

        /*
         * 1. group neighbors into a map: "name" -> (list of observations)
         * 2. calculate the largest group
         * 3. remove all entries with list smaller than largest group
         * 4. convert the key set to a list
         * 5. randomly pick a name from the list
         * 6. set the name as the guess
         */

        Map<String, List<Observation>> nghMap = neighbors.stream()
                .collect(Collectors.groupingBy(n -> n.getName()));
        int largestGroup = nghMap.entrySet().stream()
                .max(Map.Entry.comparingByValue((l1, l2) -> Integer.compare(l1.size(), l2.size())))
                .get()
                .getValue()
                .size();
        nghMap.entrySet().removeIf(e -> e.getValue().size() != largestGroup);
        int possibleChoices = nghMap.entrySet().size();
        int choice = (int) (Math.random() * (possibleChoices));
        String guess = new ArrayList<>(nghMap.keySet()).get(choice);
        o.setGuessedName(guess);
    }

    private static double calculateDistSquared(Observation o1, Observation o2) {
        if (o1.getProps().length != o2.getProps().length) {
            System.out.println("[!] Can't compare vectors of different dimensions");
            return 0;
        }
        double distanceSq = 0;
        for (int i = 0; i < o1.getProps().length; i++) {
            distanceSq += Math.pow((o1.getProps()[i] - o2.getProps()[i]), 2);
        }
        return distanceSq;
    }

    private static List<Observation> findNearestNeighbors(int k, Observation o, List<Observation> training) {
        training.sort((o1, o2) -> Double.compare(calculateDistSquared(o, o1), calculateDistSquared(o, o2)));
        if (k > training.size()) {
            System.out.println("[!] k can't be bigger than training set size");
            return null;
        }
        List<Observation> neighbors = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            neighbors.add(training.get(i));
        }
        return neighbors;
    }

    private static List<Observation> loadObsFromFile(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        int colCount = 0;
        int propCount = 0;
        List<Observation> observations = new ArrayList<>();

        while (sc.hasNextLine()) {
            String[] row = sc.nextLine().split(",");
            if (colCount == 0) {
                colCount = row.length;
                propCount = colCount - 1;
            }

            String name = row[propCount];
            double[] properties = new double[propCount];

            for (int i = 0; i < propCount; i++) {
                properties[i] = Double.parseDouble(row[i]);
            }
            observations.add(new Observation(name, properties));
        }
        sc.close();

        return observations;
    }
}
