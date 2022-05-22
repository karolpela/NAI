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
                var voOptional = featureOutcomes.stream()
                        .filter(vo -> vo.feature.equals(feature) && vo.outcome.equals(o.outcome))
                        .findFirst();
                if (voOptional.isPresent()) {
                    voOptional.get().count++;
                } else {
                    var newValueOutcome = new FeatureOutcome(columnIndex++, feature, o.outcome);
                    newValueOutcome.count++;
                    featureOutcomes.add(newValueOutcome);
                }
            }
        }

        Path testPath = Path.of(args[1]);
        var testObs = Observation.loadObsFromPath(testPath, true);

        for (Observation o : testObs) {
            System.out.println(o + " --> " + o.classify(featureOutcomes));
        }
    }
}
