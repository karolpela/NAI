package nai.zadanie4;

import static java.util.Comparator.comparing;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Main {
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

        int i = 0;
        while (i < 10) {
            // calculate new centroids
            clusters.forEach(Cluster::updateCentroid);

            // reassign observations
            clusters.forEach(Cluster::clear);
            for (Observation o : observations) {
                Cluster nearest = null;
                double minDistance = 0;
                for (Cluster cluster : clusters) {
                    double distance = Point.calculateDistSquared(o, cluster.getCentroid());
                    System.out.println(o + " to " + cluster + " distance: " + distance);
                    if (minDistance == 0) {
                        minDistance = distance;
                    }
                    if (distance < minDistance) {
                        distance = minDistance;
                        nearest = cluster;
                    }
                }
                System.out.println("NEAREST: " + nearest);
                System.out.println("---------------------------------------------");
                // Optional<Cluster> nearest =
                // clusters.stream()
                // .min(Comparator.comparingDouble(
                // c -> Point.calculateDistSquared(c.getCentroid(), o)));
                // Cluster c = null;
                // if (nearest.isPresent()) {
                // c = nearest.get();
                // c.addObservation(o);
                // }
                // System.out.println(nearest);
            }
            for (int j = 0; j < k; j++) {
                System.out.println("Cluster " + j + " " + clusters.get(j).calculateWcv());
            }
        }
    }
}
