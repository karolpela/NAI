import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

        int groupCount = (int) trainingObs.stream()
                .map(o -> o.getName())
                .distinct()
                .count();
        System.out.println("Number of groups: " + groupCount);

        int correctGuesses = 0;
        for (Observation o : testObs) {
            List<Observation> nns = findNearestNeighbors(k, o, trainingObs);
            classify(o, nns);
            System.out.println(o + " --> " + o.getGuessedName());
            if (o.getGuessedName().equals(o.getName())) correctGuesses++;
        }
        System.out.printf("Accuracy: " + correctGuesses + "/" + testObs.size()
            + " (" + "%.2f" + ")%%%n", (double)correctGuesses / testObs.size() * 100);

        int totalInputs = testObs.size();
        Scanner consoleScanner = new Scanner(System.in);
        System.out.println("[i] Enter additional comma-separated values below,");
        System.out.println("[i] Format: p1, p2, p3, ..., name");
        System.out.println("[i] or type \"quit\" to terminate the program.");
        System.out.print("->");
        while (true) {
            String input = consoleScanner.nextLine();
            if (input.equals("quit")) break;

            totalInputs++;
            String[] row = input.split(",");
            int colCount = row.length - 1;
            int propCount = colCount - 1;

            String name = row[colCount];
            double[] properties = new double[propCount];

            for (int i = 0; i < propCount; i++) {
                properties[i] = Double.parseDouble(row[i]);
            }
            Observation o = new Observation(name, properties);
            classify(o, trainingObs);

            System.out.println(o + " --> " + o.getGuessedName());
            if (o.getGuessedName().equals(o.getName())) correctGuesses++;
            System.out.printf("Accuracy: " + correctGuesses + "/" + totalInputs
            + " (" + "%.2f" + ")%%%n", (double)correctGuesses / totalInputs * 100);
        }
        consoleScanner.close();
    }

    private static void classify (Observation o, List<Observation> neighbors) {

        /* 
        1. group neighbors into a map: "name" -> (list of observations)
        2. get the entry set
        3. get the "max" entry by comparing the size of lists
        4. get its name
        5. set the name as the guess
        */

        String mostCommon = neighbors.stream()
                .collect(Collectors.groupingBy(n -> n.getName()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue((l1, l2)-> Integer.compare(l1.size(), l2.size())))
                .get()
                .getKey();
        o.setGuessedName(mostCommon);
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
                colCount = row.length - 1;
                propCount = colCount - 1;
            }

            String name = row[colCount];
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
