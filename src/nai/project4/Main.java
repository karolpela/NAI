package nai.project4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Main {
    private static final boolean VERBOSE = false;

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

        List<Point> prevCentroids = new ArrayList<>();
        List<Point> centroids = new ArrayList<>();

        int i = 0;
        while (true) {
            // save previous centroids
            prevCentroids = new ArrayList<>(centroids);

            // calculate new centroids
            clusters.forEach(Cluster::updateCentroid);

            centroids = clusters.stream()
                    .map(Cluster::getCentroid)
                    .toList();

            // see if centroids changed and break if didn't
            if (centroids.equals(prevCentroids)) {
                break;
            }

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
            i++;
        }

        if (VERBOSE) {
            for (int j = 0; j < k; j++) {
                System.out.println("\n=== Cluster " + j + " ===");
                clusters.get(j).getObservations().forEach(System.out::println);
            }
        }
    }
}
