package nai.zadanie3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class Main {

	public static final int VECTOR_SIZE = 26;

	public static void main(String[] args) throws IOException {
		Path dataDir = Path.of(new File("").getAbsolutePath() + "/" + args[0]);

		// Create a map as "language directory -> list of texts"
		Map<String, List<Path>> trainingFiles = mapFilesToDirs(dataDir);

		List<Perceptron> networkLayer = new ArrayList<>();

		for (Entry<String, List<Path>> e : trainingFiles.entrySet()) {
			String language = e.getKey();
			List<Path> textfiles = e.getValue();

			Perceptron perceptron = new Perceptron(language);
			perceptron.initRandom(VECTOR_SIZE);

			List<Observation> trainingObs = new ArrayList<>();

			for (Path p : textfiles) {
				double[] chars = TextFileHelper.calculateRatios(p);
				trainingObs.add(new Observation(language, chars));
			}

			perceptron.teach(trainingObs);
			networkLayer.add(perceptron);
		}

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
}
