package nai.zadanie3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {

	public static final int VECTOR_SIZE = 26;

	public static void main(String[] args) throws IOException {
		Path dataDir = Path.of(args[0]);

		/* Map lists of files to language directories */
		Map<String, List<Path>> trainingFiles = mapFilesToDirs(dataDir);

		List<Perceptron> perceptrons = new ArrayList<>();
		List<Observation> trainingObs = new ArrayList<>();

		for (Entry<String, List<Path>> e : trainingFiles.entrySet()) {
			String language = e.getKey();
			perceptrons.add(new Perceptron(language, VECTOR_SIZE));
			List<Path> textfiles = e.getValue();

			for (Path p : textfiles) {
				double[] chars = TextHelper.calculateRatios(p);
				trainingObs.add(new Observation(
						language, p.getFileName().toString(), chars));
			}
		}

		/* Initialize p with random values and train on all files */
		for (Perceptron p : perceptrons) {
			p.initRandom();
			System.out.println("====" + p.act + "====");
			for (int i = 0; i < 10; i++) {
				p.teach(trainingObs);
			}
			p.normalizeWeights();
		}
		getFromConsole(perceptrons);
	}

	public static Map<String, List<Path>> mapFilesToDirs(Path root) throws IOException {
		Map<String, List<Path>> map = new HashMap<>();
		try (Stream<Path> pathStream = Files.walk(root, 1)) {
			List<Path> langDirs = pathStream
					.filter(Files::isDirectory)
					.filter(p -> p != root)
					.toList();

			for (Path dir : langDirs) {
				try (Stream<Path> fileStream = Files.list(dir)) {
					map.put(dir.getFileName().toString(), fileStream.toList());
				}
			}
		}
		return map;
	}

	public static void getFromConsole(List<Perceptron> perceptrons) {
		Scanner s = new Scanner(System.in);
		System.out.println("[i] Enter text below, or type \"quit\":");
		while (true) {
			System.out.print("-> ");
			String input = s.nextLine();

			if (input.equals("quit"))
				break;

			double[] ratios = TextHelper.calculateRatios(input);
			ratios = Perceptron.normalize(ratios);

			String lang = "";
			double maxNet = 0;
			boolean set = false;

			for (Perceptron p : perceptrons) {
				double net = p.calculateNet(ratios);
				int y = p.calculateY(net);
				if (y == 1) {
					System.out.println(p.act + " " + net);
					if (!set || net > maxNet) {
						set = true;
						maxNet = net;
						lang = p.act;
					}
				}
			}
			System.out.println(lang);
		}
		s.close();
	}
}
