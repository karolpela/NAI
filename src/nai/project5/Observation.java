package nai.project5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Observation {
    List<String> features;
    String outcome;

    public Observation(List<String> features, String outcome) {
        this.features = features;
        this.outcome = outcome;
    }

    public String classify(List<FeatureOutcome> featureOutcomes) {

        Map<String, List<FeatureOutcome>> featureOutcomesByOutcome = featureOutcomes.stream()
                .collect(Collectors.groupingBy(vo -> vo.outcome));

        // Map structure:
        // play -> {sunny, play, <count>}, {overcast, play, <count>}, {rain, play, <count>}
        // {hot, play, <count>}, {mild, play, <count>}, {cool, play, <count>},
        // ...

        double maxProbability = 0.0;
        String resultClass = null;

        for (Entry<String, List<FeatureOutcome>> outcomeTotals : featureOutcomesByOutcome
                .entrySet()) {
            String currentOutcome = outcomeTotals.getKey();

            // Calculate total number of occurences of this outcome
            int featureCount = features.size();
            long totalOutcomeCount = outcomeTotals.getValue().stream()
                    .map(fo -> fo.count)
                    // Divide the result as there's an entry for every feature for each outcome
                    .reduce(0, Integer::sum) / featureCount;

            // Now get all the possible values for this outcome
            // and leave only the ones relevant to this observation
            List<FeatureOutcome> obsFeatureOutcomes = outcomeTotals.getValue().stream()
                    .filter(fo -> features.contains(fo.feature))
                    .toList();

            // Map structure after filtering:
            // play -> {sunny, play, <count>}, {hot, play, <count>}
            // {high, play, <count>}, {false,play, <count>}

            double probability = 1;


            AtomicInteger columnIndex = new AtomicInteger();
            for (String feature : features) {
                // Try to find such case in data from training
                var foOptional = obsFeatureOutcomes.stream()
                        .filter(fo -> fo.outcome.equals(currentOutcome)
                                && fo.feature.equals(feature)
                                && fo.columnIndex == columnIndex.get())
                        .findFirst();
                int count = foOptional.isEmpty() ? 0 : foOptional.get().count;

                // Laplace smoothing
                if (count == 0) {
                    long possibleFeatureCount = featureOutcomes.stream()
                            .filter(fo -> fo.columnIndex == columnIndex.get())
                            .count();
                    count = 1;
                    totalOutcomeCount += possibleFeatureCount;
                }

                // None can be zero after above operations
                probability *= ((double) count / totalOutcomeCount);
                columnIndex.incrementAndGet();
            }
            if (probability > maxProbability) {
                maxProbability = probability;
                resultClass = currentOutcome;
            }
        }
        return resultClass;
    }

    public static List<Observation> loadObsFromPath(Path path, boolean isTest) throws IOException {
        List<Observation> observations = new ArrayList<>();
        try (var lineStream = Files.lines(path)) {
            lineStream.forEach(l -> {
                String[] row = l.split(",");
                List<String> features;
                String outcome = null;
                if (isTest) {
                    features = Arrays.asList(row);
                } else {
                    features = Arrays.asList(Arrays.copyOfRange(row, 0, row.length - 1));
                    outcome = row[row.length - 1];
                }
                observations.add(new Observation(features, outcome));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return observations;
    }

    @Override
    public String toString() {
        String description = "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < features.size(); i++) {
            builder.append(features.get(i) + (i != features.size() - 1 ? "," : ""));
        }
        if (outcome != null) {
            builder.append(" -> " + outcome);
        }
        return description;
    }
}
