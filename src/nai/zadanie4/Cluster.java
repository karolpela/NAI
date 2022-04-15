package nai.zadanie4;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private List<Observation> observations;
    private Point centroid;

    public Cluster() {
        this.observations = new ArrayList<>();
    }

    public boolean addObservation(Observation o) {
        return observations.add(o);
    }

    public void clear() {
        observations.clear();
    }

    public double calculateWcv() {
        double wcv = 0;
        for (Observation o1 : observations) {
            for (Observation o2 : observations) {
                if (o1 == o2)
                    continue;
                wcv += Point.calculateDistSquared(o1, o2);
            }
        }
        return wcv;
    }


    public void updateCentroid() {
        int n = observations.get(0).coordinates.length;
        double[] newCentroid = new double[n];
        for (Observation o : observations) {
            double[] props = o.getCoordinates();
            for (int i = 0; i < n; i++) {
                newCentroid[i] += props[i];
            }
        }

        int observationCount = observations.size();
        assert observationCount > 0;
        for (double d : newCentroid) {
            d = d / observationCount;
        }
        centroid = new Point(newCentroid);
    }

    public Point getCentroid() {
        return centroid;
    }
}
