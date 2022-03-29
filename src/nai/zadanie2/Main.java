package nai.zadanie2;

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
	static double[] w;

	static int k = 3; // teaching iterations

	public static void main(String[] args) throws FileNotFoundException {
		// Files and data
		File training = new File(args[1]);
		File test = new File(args[2]);
		List<Observation> trainingObs = loadObsFromFile(training);
		List<Observation> testObs = loadObsFromFile(test);

		// Parameters
		int n = trainingObs.get(0).getProps().length;
		a = 0.01;
		w = new double[n];

		// Range
		double x1 = -1;
		double x2 = 1;
		double f = 0;

		for (int i = 0; i < w.length; i++) {
			// Used for generating values in specified range
			f = Math.random() / Math.nextDown(1.0);
			double x = x1 * (1.0 - f) + x2 * f;
			w[i] = x;
		}
		w = normalize(w);
		f = Math.random() / Math.nextDown(1.0);
		t = x1 * (1.0 - f) + x2 * f;

		for (int i = 0; i < k; i++) {
			System.out.println("====== Iteration " + (i + 1) + " ======");
			System.out.println("W -> " + Arrays.toString(w));
			System.out.println("t -> " + t);
			teach(trainingObs);
		}

		System.out.println("======== Testing ========");
		classify(testObs);

		Scanner s = new Scanner(System.in);
		System.out.println("[i] Enter additional comma-separated values below,");
		System.out.println("[i] or type \"quit\" to terminate the program.");
		Map<String, Integer> classes = getClasses(trainingObs);
		while (true) {
			System.out.print("-> ");
			String input = s.nextLine();
			if (input.equals("quit"))
				break;

			double[] properties = Arrays.stream(input.split(","))
					.mapToDouble(Double::parseDouble)
					.toArray();

			Observation o = new Observation(properties);
			classes.entrySet()
					.stream()
					.filter(e -> e.getValue() == calculateY(o.getProps(), w, t))
					.findFirst()
					.ifPresent(e -> System.out.println(e.getKey()));
		}
		s.close();
	}

	// Assign 0 and 1 to names in observation list
	static Map<String, Integer> getClasses(List<Observation> obs) {
		AtomicInteger i = new AtomicInteger(0);
		return obs.stream()
				.collect(groupingBy(Observation::getName))
				.keySet()
				.stream()
				.collect(toMap(k -> k, v -> i.getAndIncrement()));
	}

	static boolean check(Observation o, Map<String, Integer> classes, boolean teach) {
		double[] x = o.getProps();
		int d = classes.get(o.getName());
		int y = calculateY(x, w, t);
		if (verbose) {
			System.out.println("X -> " + Arrays.toString(o.getProps()));
			System.out.println("W -> " + Arrays.toString(w));
			System.out.println("t -> " + t);
			System.out.println("d=" + d + " | y=" + y);
		}
		// If the answer doesn't match the expected one, adjust W and t.
		// Normalize W to avoid having gigantic vectors.
		if (d != y) {
			if (teach) {
				for (int i = 0; i < w.length; i++) {
					w[i] += (d - y) * a * x[i];
				}
				w = normalize(w);
				t += ((d - y) * a * (-1));
				if (verbose) {
					System.out.println("W' -> " + Arrays.toString(w));
					System.out.println("t' -> " + t);
					System.out.println("-------------------------");
				}
			}
			return false;
		} else {
			if (verbose) System.out.println("-------------------------");
			return true;
		}
	}

	static void teach(List<Observation> trainingObs) {
		// Assign 0 and 1 to observation names
		Map<String, Integer> classes = getClasses(trainingObs);

		int adjustments = 0;

		for (Observation o : trainingObs) {
			if (!check(o, classes, true))
				adjustments++;
		}
		System.out.printf("Mistakes made: %d/%d (" + "%.2f" + "%%)%n",
				adjustments, trainingObs.size(), (double) adjustments /trainingObs.size() * 100);
	}

	static void classify(List<Observation> testObs) {
		Map<String, Integer> classes = getClasses(testObs);

		int correctGuesses = 0;

		for (Observation o : testObs) {
			if (check(o, classes, false))
				correctGuesses++;
		}
		System.out.printf("Correct guesses: %d/%d (" + "%.2f" + "%%)%n",
				correctGuesses, testObs.size(), (double) correctGuesses / testObs.size() * 100);
	}

	private static int calculateY(double[] x, double[] w, double t) {
		double z = 0;
		for (int i = 0; i < x.length; i++) {
			z += x[i] * w[i];
		}
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
		double l = getLength(v);
		for (int i = 0; i < v.length; i++) {
			v[i] = (v[i] / l);
		}
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
