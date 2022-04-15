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
        for (Observation o : observations) {
            wcv += Point.calculateDistSquared(o, centroid);
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

        for (int i = 0; i < newCentroid.length; i++) {
            newCentroid[i] /= observationCount;
        }
        centroid = new Point(newCentroid);
    }

    public Point getCentroid() {
        return centroid;
    }

    public List<Observation> getObservations() {
        return observations;
    }
}
