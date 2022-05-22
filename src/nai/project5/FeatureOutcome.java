package nai.project5;

public class FeatureOutcome {
    String feature;
    String outcome;
    int count;
    int columnIndex;

    public FeatureOutcome(int columnIndex, String feature, String outcome) {
        this.columnIndex = columnIndex;
        this.feature = feature;
        this.outcome = outcome;
    }
}
