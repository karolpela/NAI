public class Observation {
    String name;
    double[] properties;

    public Observation(String name, double[] properties ) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getProperties() {
        return properties;
    }

    public void setProperties(double[] properties) {
        this.properties = properties;
    }

    
}