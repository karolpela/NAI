package nai.zadanie3;

import java.util.Arrays;
import java.util.List;

public class Perceptron {
	static final boolean SUMMARY = true;
	static final boolean VERBOSE = false;

	static double a;

	String act; // the class for which 1 is expected on output
	double[] w;
	double t;

	public Perceptron(String act) {
		this.act = act;
		this.w = new double[0];
		this.t = 0;
	}

	// Check if output for properties of observation o matches the expected one.
	boolean check(Observation o) {
		double[] x = o.getProps();
		int d = getD(o);// 0 is the expeted result for unknown class
		int y = calculateY(calculateNet(x));
		if (VERBOSE) {
			System.out.println("X -> " + Arrays.toString(x));
			System.out.println("W -> " + Arrays.toString(w));
			System.out.println("t -> " + t);
			System.out.println("d=" + d + " | y=" + y);
		}
		return (d == y);
	}

	private void learn(Observation o) {
		double[] x = o.getProps();
		int d = getD(o);
		int y = calculateY(calculateNet(x));

		updateWeights(x, d, y);
		updateBias(d, y);
	}

	private int getD(Observation o) {
		return o.getName().equals(act) ? 1 : 0;
	}

	private void updateWeights(double[] x, int d, int y) {
		for (int i = 0; i < w.length; i++) {
			w[i] += (d - y) * a * x[i];
		}
		w = normalize(w);
		if (VERBOSE)
			System.out.println("W' -> " + Arrays.toString(w));
	}

	private void updateBias(int d, int y) {
		t += ((d - y) * a * (-1));
		if (VERBOSE)
			System.out.println("t' -> " + t);
	}

	public void teach(List<Observation> trainingObs) {

		int adjustments = 0;

		for (Observation o : trainingObs) {
			if (!check(o)) {
				adjustments++;
				learn(o);
			}
		}
		if (SUMMARY)
			System.out.printf("Mistakes made: %d/%d (" + "%.2f" + "%%)%n",
					adjustments, trainingObs.size(), (double) adjustments / trainingObs.size() * 100);
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

	public void initRandom(int n) {
		w = new double[n];
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
		w = normalize(w);
		f = Math.random() / Math.nextDown(1.0);
		t = x1 * (1.0 - f) + x2 * f;
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

	public double[] normalize(double[] v) {
		double l = Observation.getLength(v);
		for (int i = 0; i < v.length; i++) {
			v[i] = (v[i] / l);
		}
		return v;
	}
}
