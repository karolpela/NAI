package nai.zadanie4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Main {
    private static final boolean VERBOSE = false;
    private static final int ITERATIONS = 5;

    public static void main(String[] args) throws FileNotFoundException {
        // process arguments
        List<Observation> observations = Observation.loadObsFromFile(new File(args[0]));

        int k = Integer.parseInt(args[1]);

        // create k clusters
        List<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            clusters.add(new Cluster());
        }

        // randomly assign observations to clusters
        Random r = new Random();
        for (Observation o : observations) {
            int c = r.nextInt(k);
            clusters.get(c).addObservation(o);
        }

        for (int i = 0; i < ITERATIONS; i++) {
            // calculate new centroids
            clusters.forEach(Cluster::updateCentroid);

            // clear assignments
            clusters.forEach(Cluster::clear);

            // reassign every observation
            for (Observation o : observations) {
                // find nearest centroid by comparing distance
                Optional<Cluster> nearest =
                        clusters.stream()
                                .min(Comparator.comparingDouble(
                                        c -> Point.calculateDistSquared(c.getCentroid(), o)));
                if (nearest.isPresent()) {
                    nearest.get().addObservation(o);
                }
            }
            System.out.println("--- Iteration " + (i + 1) + " ---");
            for (int j = 0; j < k; j++) {
                System.out.println("Cluster " + j + ": " + clusters.get(j).calculateWcv());
            }

        }

        if (VERBOSE) {
            for (int i = 0; i < k; i++) {
                System.out.println("=== Cluster " + i + " ===");
                clusters.get(i).getObservations().forEach(System.out::println);
            }
        }
    }
}
