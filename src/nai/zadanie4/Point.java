package nai.zadanie4;

import java.util.Arrays;

public class Point {
    double[] coordinates;

    public Point(double[] coordinates) {
        this.coordinates = coordinates;
    }


    public double[] getCoordinates() {
        return coordinates;
    }

    public static double calculateDistSquared(Point p1, Point p2) {
        if (p1.getCoordinates().length != p2.getCoordinates().length) {
            System.out.println("[!] Can't compare vectors of different lengths");
            return 0;
        }

        double distanceSq = 0;
        for (int i = 0; i < p1.getCoordinates().length; i++) {
            distanceSq += Math.pow((p1.getCoordinates()[i] - p2.getCoordinates()[i]), 2);
        }
        // System.out.println(p1 + ":" + p2 + " " + distanceSq);
        return distanceSq;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(coordinates);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        return Arrays.equals(coordinates, other.coordinates);
    }
}
