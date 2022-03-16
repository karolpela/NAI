import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        int k = Integer.parseInt(args[0]);
        File training = new File(args[1]);
        File test = new File(args[2]);

        List<Observation> trainingObs = loadObsFromFile(training);
        List<Observation> testObs = loadObsFromFile(test);

        int groupCount = (int) trainingObs.stream()
                .map(o -> o.getName())
                .distinct()
                .count();
        System.out.println("Number of groups: " + groupCount);
        

    }

    private static List<Observation> loadObsFromFile(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        int argCount = 0;
        List<Observation> observations = new ArrayList<>();

        while (sc.hasNextLine()) {
            String[] row = sc.nextLine().split(",");
            if (argCount == 0) {
                argCount = row.length - 1;
            }

            int propCount = argCount - 1;
            String name = row[argCount];
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
