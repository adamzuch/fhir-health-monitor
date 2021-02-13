package Model;

/**
 * A form of adapter which contains health observation data returned from the server.
 */
public class Observation {
    private String value;
    private String time;
    private boolean aboveThreshold;

    public Observation(String value, String time) {
        this.value = value;
        this.time = time;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAboveThreshold(boolean aboveThreshold) {
        this.aboveThreshold = aboveThreshold;
    }

    public String getValue() {
        return value;
    }

    public String getTime() {
        return time;
    }

    public boolean isAboveThreshold() {
        return aboveThreshold;
    }
}
