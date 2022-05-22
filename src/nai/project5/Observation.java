package nai.project5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Observation {
    String[] features;
    String outcome;


    public Observation(String[] features, String outcome) {
        this.features = features;
        this.outcome = outcome;
    }


    public static List<Observation> loadObsFromPath(Path path) throws IOException {
        List<Observation> observations = new ArrayList<>();
        try (var lineStream = Files.lines(path)) {
            lineStream.forEach(l -> {
                String[] row = l.split(",");
                String[] features = Arrays.copyOfRange(row, 0, row.length - 2);
                String decision = row[row.length - 1];
                observations.add(new Observation(features, decision));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return observations;
    }
}
