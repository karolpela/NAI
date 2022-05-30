package nai.project6;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static boolean[] optimalKnapsack = new boolean[0];
    static int optimalKnapsackValue = 0;
    static int optimalKnapsackWeight = 0;

    public static void main(String[] args) throws IOException {
        Path dataPath = Path.of(args[0]);

        int k = 0;
        int n = 0;
        int[] values = null;
        int[] weights = null;

        try (var scanner = new Scanner(dataPath)) {
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 0:
                        k = scanner.nextInt();
                        n = scanner.nextInt();
                        scanner.nextLine();
                        break;
                    case 1:
                        weights = Arrays.stream(scanner.nextLine().split(","))
                                .mapToInt(Integer::parseInt)
                                .toArray();
                        break;
                    case 2:
                        values = Arrays.stream(scanner.nextLine().split(","))
                                .mapToInt(Integer::parseInt)
                                .toArray();
                        break;
                    default:
                        break;
                }
            }
        }
        boolean[] knapsack = new boolean[n];
        optimalKnapsackValue = pack(0, k, weights, values, knapsack);

        System.out.println(
                "Optimal knapsack:\t\t" + bitArrayToStringHighlighted(optimalKnapsack, -1));
        System.out.println("Optimal knapsack value:\t\t" + optimalKnapsackValue);
        System.out.println("Optimal knapsack weight:\t" + optimalKnapsackWeight);
    }

    public static int pack(int i, int capacity, int[] weights, int[] values, boolean[] knapsack) {
        if (values == null || weights == null)
            return 0;

        // print the array with changes from last method call included
        System.out.println(bitArrayToStringHighlighted(knapsack, i - 1));
        System.out.println("Current index: " + i);

        // i is the index of current item
        // capacity is remaining capacity

        // base case
        if (i == values.length - 1 || capacity == 0) {
            System.out.println();
            System.out.println("----------- Reached end of current path -----------");

            if (weights[i] <= capacity) {
                knapsack[i] = true;
            } else {
                knapsack[i] = false;
            }

            // zero all items past the end of current path
            for (int j = i + 1; j < knapsack.length; j++) {
                knapsack[j] = false;
            }

            int currentKnapsackValue = sumArray(knapsack, values);
            int currentKnapsackWeight = sumArray(knapsack, weights);

            System.out.println("\nBest knapsack so far:");
            System.out.println(
                    bitArrayToStringHighlighted(optimalKnapsack, -1)
                            + "\t Value: " + optimalKnapsackValue
                            + "\t Weight: " + optimalKnapsackWeight);
            System.out.println("\nCurrent knapsack:");
            System.out.println(
                    bitArrayToStringHighlighted(knapsack, i)
                            + "\t Value: " + currentKnapsackValue
                            + "\t Weight: " + currentKnapsackWeight);

            // check if the current solution is better than the one found so far
            // first by value, then by weight if value is equal
            if (currentKnapsackValue > optimalKnapsackValue ||
                    currentKnapsackValue == optimalKnapsackValue
                            && currentKnapsackWeight < optimalKnapsackWeight) {

                System.out.println("\nNew best knapsack found!");
                optimalKnapsack = Arrays.copyOf(knapsack, knapsack.length);
                optimalKnapsackValue = currentKnapsackValue;
                optimalKnapsackWeight = currentKnapsackWeight;
            }
            System.out.println("---------------------------------------------------\n");
            if (knapsack[i]) {
                return values[i];
            } else {
                return 0;
            }
        }

        // check if element can fit
        if (weights[i] <= capacity) {

            // item is included
            int spaceIfIncluded = capacity - weights[i];
            knapsack[i] = true;
            int included = values[i] + pack(i + 1, spaceIfIncluded, weights, values, knapsack);

            // item is skipped
            knapsack[i] = false;
            int excluded = pack(i + 1, capacity, weights, values, knapsack);

            // save the better option
            knapsack[i] = included > excluded;

            if (knapsack[i]) {
                return included;
            } else {
                return excluded;
            }
        } else {
            // the element can't fit
            knapsack[i] = false;
            return pack(i + 1, capacity, weights, values, knapsack);
        }
    }

    public static int sumArray(boolean[] knapsack, int[] values) {
        int total = 0;
        for (int i = 0; i < knapsack.length; i++) {
            if (knapsack[i])
                total += values[i];
        }
        return total;
    }

    public static String bitArrayToStringHighlighted(boolean[] array, int highlight) {
        String[] textArray = new String[array.length];
        for (int i = 0; i < textArray.length; i++) {
            String current = array[i] ? "1" : "0";
            if (i == highlight)
                current = "|" + current + "|";
            textArray[i] = current;
        }
        return Arrays.toString(textArray);
    }
}
