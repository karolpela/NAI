package nai.project5;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        var trainingPath = Path.of(args[0]);
        var trainingObs = Observation.loadObsFromPath(trainingPath, false);

        List<FeatureOutcome> featureOutcomes = new ArrayList<>();

        for (Observation o : trainingObs) {
            int columnIndex = 0;
            for (String feature : o.features) {
                var foOptional = featureOutcomes.stream()
                        .filter(fo -> fo.feature.equals(feature) && fo.outcome.equals(o.outcome))
                        .findFirst();
                if (foOptional.isPresent()) {
                    foOptional.get().count++;
                } else {
                    var newFeatureOutcome = new FeatureOutcome(columnIndex, feature, o.outcome);
                    newFeatureOutcome.count++;
                    featureOutcomes.add(newFeatureOutcome);
                }
                columnIndex++;
            }
        }

        var testPath = Path.of(args[1]);
        var testObs = Observation.loadObsFromPath(testPath, true);

        for (Observation o : testObs) {
            System.out.println(o + " -> " + o.classify(featureOutcomes));
        }

        var scanner = new Scanner(System.in);
        System.out.println("[i] Enter additional comma-separated values below");
        System.out.println("[i] or type \"quit\" to terminate the program.");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.equals("quit"))
                break;

            List<String> features = Arrays.asList(input.split(","));
            Observation o = new Observation(features, null);
            System.out.print("-> " + o.classify(featureOutcomes) + '\n');
        }
        scanner.close();
    }
}
