package NAI2;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
	static boolean verbose = false;

	static double a;
	static double t;
	static double[] W;

	static int k = 50; // teaching iterations

	public static void main(String[] args) throws FileNotFoundException {
		// Files and data
		File training = new File(args[1]);
		File test = new File(args[2]);
		List<Observation> trainingObs = loadObsFromFile(training);
		List<Observation> testObs = loadObsFromFile(test);

		// Parameters
		int n = trainingObs.get(0).getProps().length;
		// a = Double.parseDouble(args[0]);
		a = 0.01;
		W = new double[n];

		// Range
		double x1 = 1;
		double x2 = 10;
		// Used for generating values in specified range
		double f = Math.random() / Math.nextDown(1.0);

		for (int i = 0; i < W.length; i++) {
			double x = x1 * (1.0 - f) + x2 * f;
			W[i] = x;
		}
		t = x1 * (1.0 - f) + x2 * f;

		for (int i = 0; i < k; i++) {
			System.out.println("====== Iteration " + (i + 1) + " ======");
			System.out.println("W -> " + Arrays.toString(W));
			System.out.println("t -> " + t);
			teach(a, trainingObs);
			System.out.println("-------------------------");
		}

		System.out.println("======== Testing ========");
		classify(testObs);

		Scanner s = new Scanner(System.in);
		System.out.println("[i] Enter additional comma-separated values below,");
		System.out.println("[i] or type \"quit\" to terminate the program.");
		Map<String, Integer> classes = getClasses(trainingObs);
		while (true) {
			System.out.print("->");
			String input = s.nextLine();
			if (input.equals("quit"))
				break;

			double[] properties = Arrays.stream(input.split(","))
					.mapToDouble(Double::parseDouble)
					.toArray();

			Observation o = new Observation(properties);
			System.out.println(
					classes.entrySet()
							.stream()
							.filter(e -> e.getValue() == calculateY(o.getProps(), W, t))
							.findFirst()
							.get()
							.getKey());
		}
		s.close();
	}

	// Assign 0 and 1 to names in observation list
	static Map<String, Integer> getClasses(List<Observation> obs) {
		AtomicInteger i = new AtomicInteger(0);
		return obs.stream()
				.collect(groupingBy(o -> o.getName()))
				.keySet()
				.stream()
				.collect(toMap(k -> k, v -> i.getAndIncrement()));
	}

	static boolean check(Observation o, Map<String, Integer> classes, boolean teach) {
		double[] X = o.getProps();
		int d = classes.get(o.getName());
		int y = calculateY(X, W, t);
		// If the answer doesn't match the expected one, adjust W and t.
		// Normalize W to avoid having gigantic vectors.
		if (d != y) {
			if (verbose) {
				System.out.println("X -> " + Arrays.toString(o.getProps()));
				System.out.println("W -> " + Arrays.toString(W));
				System.out.println("t -> " + t);
				System.out.println("d=" + d + " | y=" + y);
			}

			if (teach) {
				for (int i = 0; i < W.length; i++) {
					W[i] += (d - y) * a * X[i];
				}
				if (getLength(W) > 1)
					normalize(W);
				W = normalize(W);
				t += ((d - y) * a * (-1));
			}

			if (verbose) {
				System.out.println("W' -> " + Arrays.toString(W));
				System.out.println("t' -> " + t);
				System.out.println("----------------------");
			}
			return false;
		} else {
			return true;
		}
	}

	static void teach(double a, List<Observation> trainingObs) {
		// Assign 0 and 1 to observation names
		Map<String, Integer> classes = getClasses(trainingObs);

		int adjustments = 0;

		for (Observation o : trainingObs) {
			if (!check(o, classes, true))
				adjustments++;
		}
		System.out.printf("Mistakes made: " + adjustments + "/" + trainingObs.size() + " (" + "%.2f" + "%%)%n",
				(double) adjustments / trainingObs.size() * 100);
	}

	static void classify(List<Observation> testObs) {
		Map<String, Integer> classes = getClasses(testObs);

		int correctGuesses = 0;

		for (Observation o : testObs) {
			if (check(o, classes, true))
				correctGuesses++;
		}
		System.out.printf("Correct guesses: " + correctGuesses + "/" + testObs.size() + " (" + "%.2f" + "%%)%n",
				(double) correctGuesses / testObs.size() * 100);
	}

	private static int calculateY(double[] X, double[] W, double t) {
		double z = 0;
		for (int i = 0; i < X.length; i++) {
			z += X[i] * W[i];
		}
		// if (verbose) System.out.println("z -> " + z);
		return (z > t) ? 1 : 0;
	}

	private static double getLength(double[] v) {
		double l = 0;
		for (int i = 0; i < v.length; i++) {
			l += Math.pow(v[i], 2);
		}
		return Math.sqrt(l);
	}

	private static double[] normalize(double[] v) {
		// System.out.println(Arrays.toString(v));
		double l = getLength(v);
		for (int i = 0; i < v.length; i++) {
			v[i] = (v[i] / l);
		}
		// System.out.println("Normalized: " + Arrays.toString(v));
		return v;
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
