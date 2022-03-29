package nai.zadanie3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Main {
	
	public static void main(String[] args) throws IOException {
		Path dataDir = Path.of(new File("").getAbsolutePath() + "/" + args[0]);
		Map<String, List<Path>> trainingFiles = new HashMap<>();

		try (Stream<Path> pathStream = Files.walk(dataDir, 1)) {
			List<Path> languages = pathStream
				.filter(Files::isDirectory)
				.filter(p -> p != dataDir)
				.toList();

			for (Path langDir : languages) {
				try (Stream<Path> fileStream = Files.list(langDir)) {
					trainingFiles.put(
						langDir.getFileName().toString(), 
						fileStream.toList());
				}
			}
		}

		trainingFiles.entrySet().forEach(System.out::println);

	}
}
