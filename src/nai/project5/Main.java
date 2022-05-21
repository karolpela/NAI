package nai.project5;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Path trainingData = Path.of(args[0]);
        var observations = Observation.loadObsFromPath(trainingData);
    }
}
