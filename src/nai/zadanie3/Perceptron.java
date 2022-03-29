package nai.zadanie3;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;

public class Perceptron {
	static boolean verbose = false;

	static double a;

	double[] w;
	double t;

	public Perceptron(double[] w, double t) {
		this.w = w;
		this.t = t;
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
		
		// Check if output for properties of observation o matches the expected one.
		// If not, adjust W and t if the perceptron is in teaching mode.
		boolean check(Observation o, Map<String, Integer> classes, boolean teach) {
			double[] x = o.getProps();
			int d = classes.get(o.getName());
			int y = calculateOut(x);
			if (verbose) {
				System.out.println("X -> " + Arrays.toString(x));
				System.out.println("W -> " + Arrays.toString(w));
				System.out.println("t -> " + t);
				System.out.println("d=" + d + " | y=" + y);
			}
			boolean correct = (d == y);
			if (!correct && teach) {
				for (int i = 0; i < w.length; i++) {
					w[i] += (d - y) * a * x[i];
				}
				w = normalize(w);
				t += ((d - y) * a * (-1));
				if (verbose) {
					System.out.println("W' -> " + Arrays.toString(w));
					System.out.println("t' -> " + t);
				}
			}
			if (verbose) System.out.println("-------------------------");
			return correct;
		}

	void teach(List<Observation> trainingObs) {
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

	void classify(List<Observation> testObs) {
		Map<String, Integer> classes = getClasses(testObs);

		int correctGuesses = 0;

		for (Observation o : testObs) {
			if (check(o, classes, false))
				correctGuesses++;
		}
		System.out.printf("Correct guesses: %d/%d (" + "%.2f" + "%%)%n",
				correctGuesses, testObs.size(), (double) correctGuesses / testObs.size() * 100);
	}
	
	public int calculateOut(double[] x) {
		double z = 0;
		for (int i = 0; i < x.length; i++) {
			z += x[i] * w[i];
		}
		return (z > t) ? 1 : 0;
	}

	public double[] normalize(double[] v) {
		double l = Observation.getLength(v);
		for (int i = 0; i < v.length; i++) {
			v[i] = (v[i] / l);
		}
		return v;
	}
}
