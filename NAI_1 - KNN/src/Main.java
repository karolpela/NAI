import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        int k           = Integer.parseInt(args[0]);
        File training   = new File(args[1]);
        File test       = new File(args[2]);
        
        Scanner sc = new Scanner(training);
        int argCount = 0;
        List<Observation> observations = new ArrayList<>();


        while(sc.hasNextLine()) {
            String[] row = sc.nextLine().split(",");
            if (argCount == 0) {
                argCount = row.length - 1;
            }
            String name = row[argCount];

            int propCount = argCount - 1;
            double[] properties = new double[propCount];

            for (int i = 0; i < propCount - 1; i++) {
                properties[i] = Double.parseDouble(row[i]);
            }
            observations.add(new Observation(name, properties));
        }
        sc.close();
    }
}
