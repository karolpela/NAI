package nai.project3;

import java.util.Arrays;
import java.util.List;

public class Perceptron {
	static final boolean SUMMARY = true;
	static final boolean VERBOSE = false;

	static double a = 0.1;

	String act; // the class for which 1 is expected on output
	double[] w;
	double t;

	public Perceptron(String act, int n) {
		this.act = act;
		this.w = new double[n];
		this.t = 0;
	}

	// Check if output for properties of observation o matches the expected one.
	public boolean check(Observation o) {
		System.out.println(o.filename);
		double[] x = o.getRatios();
		int d = getD(o); // 0 is the expected result for unknown class
		int y = calculateY(calculateNet(x));
		if (VERBOSE) {
			System.out.println("X -> " + Arrays.toString(x));
			System.out.println("W -> " + Arrays.toString(w));
			System.out.println("t -> " + t);
			System.out.println("d=" + d + " | y=" + y);
		}
		if (SUMMARY) {
			System.out.println("G: " + (y == 1 ? act : "--")
					+ "    A: " + (o.getName().equals(act) ? act : "--"));
		}
		return (d == y);
	}

	private int getD(Observation o) {
		return o.getName().equals(act) ? 1 : 0;
	}

	public void teach(List<Observation> trainingObs) {
		int adjustments = 0;

		for (Observation o : trainingObs) {
			o.setRatios(normalize(o.getRatios()));
			if (!check(o)) {
				adjustments++;
				learn(o);
			}
		}
		if (SUMMARY)
			System.out.printf("Mistakes made: %d/%d (" + "%.2f" + "%%)%n",
					adjustments, trainingObs.size(),
					(double) adjustments / trainingObs.size() * 100);
	}

	private void learn(Observation o) {
		double[] x = o.getRatios();
		int d = getD(o);
		int y = calculateY(calculateNet(x));

		updateWeights(x, d, y);
		// normalizeWeights();
		updateBias(d, y);
	}

	private void updateWeights(double[] x, int d, int y) {
		for (int i = 0; i < w.length; i++) {
			w[i] += ((d - y) * a * x[i]);
		}
		if (VERBOSE)
			System.out.println("W' -> " + Arrays.toString(w));
	}

	private void updateBias(int d, int y) {
		t += ((d - y) * a * (-1));
		if (VERBOSE)
			System.out.println("t' -> " + t);
	}

	public void classify(List<Observation> testObs) {
		int correctGuesses = 0;

		for (Observation o : testObs) {
			if (check(o)) {
				correctGuesses++;
			}
		}
		if (SUMMARY)
			System.out.printf("Correct guesses: %d/%d (" + "%.2f" + "%%)%n",
					correctGuesses, testObs.size(), (double) correctGuesses / testObs.size() * 100);
	}

	public double calculateNet(double[] x) {
		double net = 0;
		for (int i = 0; i < x.length; i++) {
			net += x[i] * w[i];
		}
		return net;
	}

	public int calculateY(double net) {
		return (net > t) ? 1 : 0; // in this case a simple step function
	}

	public void normalizeWeights() {
		this.w = normalize(w);
	}

	public void initRandom() {
		// Random generation range
		double x1 = -1;
		double x2 = 1;
		double f = 0;

		for (int i = 0; i < w.length; i++) {
			// Used for generating values in specified range
			f = Math.random() / Math.nextDown(1.0);
			double x = x1 * (1.0 - f) + x2 * f;
			w[i] = x;
		}
		f = Math.random() / Math.nextDown(1.0);
		t = x1 * (1.0 - f) + x2 * f;
	}

	public static double calculateLength(double[] v) {
		double l = 0;
		for (int i = 0; i < v.length; i++) {
			l += Math.pow(v[i], 2);
		}
		l = Math.sqrt(l);
		return l;
	}

	public static double[] normalize(double[] v) {
		double[] u = Arrays.copyOf(v, v.length);
		double l = calculateLength(u);
		for (int i = 0; i < u.length; i++) {
			u[i] = (u[i] / l);
		}
		return u;
	}
}
