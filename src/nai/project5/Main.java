package nai.project5;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) throws IOException {
        Path trainingData = Path.of(args[0]);
        var observations = Observation.loadObsFromPath(trainingData);

        HashSet<ValueOutcome> valueOutcomes = new HashSet<>();

        for (Observation o : observations) {
            for (String value : o.features) {
                var voOptional = valueOutcomes.stream()
                        .filter(vo -> vo.value.equals(value) && vo.outcome.equals(o.outcome))
                        .findFirst();
                if (voOptional.isPresent()) {
                    voOptional.get().count++;
                } else {
                    var newValueOutcome = new ValueOutcome(value, o.outcome);
                    newValueOutcome.count++;
                    valueOutcomes.add(newValueOutcome);
                }
            }
        }
    }
}
