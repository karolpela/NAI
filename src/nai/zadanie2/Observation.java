package nai.zadanie2;

import java.util.Arrays;

public class Observation {
    String name;
    String guessedName;
    double[] props;

    public Observation(double[] properties ) {
        this.props = properties;
    }

    public Observation(String name, double[] properties ) {
        this.name = name;
        this.props = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getProps() {
        return props;
    }

    public void setProps(double[] properties) {
        this.props = properties;
    }

    public String getGuessedName() {
        return guessedName;
    }

    public void setGuessedName(String guessedName) {
        this.guessedName = guessedName;
    }

    @Override
    public String toString() {
        return (name != null ? name + " " : "") + Arrays.toString(props);
    }
    
}