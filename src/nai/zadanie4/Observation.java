package nai.zadanie4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Observation extends Point {
    String name;

    public Observation(double[] properties) {
        super(properties);
    }

    public Observation(String name, double[] properties) {
        this(properties);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return (name != null ? name + " " : "") + Arrays.toString(coordinates);
    }


    public static List<Observation> loadObsFromFile(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        int colCount = 0;
        int propCount = 0;
        List<Observation> observations = new ArrayList<>();

        while (sc.hasNextLine()) {
            String[] row = sc.nextLine().split(",");
            if (colCount == 0) {
                colCount = row.length;
                propCount = colCount - 1;
            }

            String name = row[propCount];
            double[] properties = new double[propCount];

            for (int i = 0; i < propCount; i++) {
                properties[i] = Double.parseDouble(row[i]);
            }
            observations.add(new Observation(name, properties));
        }
        sc.close();
        return observations;
    }
}
