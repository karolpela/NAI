package nai.project5;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

        Path testPath = Path.of(args[1]);
        var testObs = Observation.loadObsFromPath(testPath, true);

        for (Observation o : testObs) {
            System.out.println(o + " -> " + o.classify(featureOutcomes));
        }
    }
}
