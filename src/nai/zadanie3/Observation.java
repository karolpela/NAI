package nai.zadanie3;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public class Observation {
    String name;
    String filename;
    double[] ratios;

    public Observation(double[] ratios) {
        this.ratios = ratios;
    }

    public Observation(String language, String filename, double[] ratios) {
        this.name = language;
        this.filename = filename;
        this.ratios = ratios;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getRatios() {
        return ratios;
    }

    public void setRatios(double[] ratios) {
        this.ratios = ratios;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return (name != null ? name + " " : "") + Arrays.toString(ratios);
    }

    public static double sumRatios(double[] ratios) {
        return DoubleStream.of(ratios).sum();
    }
}