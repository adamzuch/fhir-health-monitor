package Model;

/**
 * Specifies the different types of observations that can be monitored.
 */
public enum ObservationType {
    CHOLESTEROL(200.0),
    SYSTOLIC_BLOOD_PRESSURE(120.0),
    DIASTOLIC_BLOOD_PRESSURE(80.0);

    private final double threshold;

    ObservationType(final double threshold) {
        this.threshold = threshold;
    }

    public double getDefaultThresholdValue() {
        return threshold;
    }

    /**
     * Translates the enum into a string.
     */
    @Override
    public String toString() {
        switch(this) {
            case CHOLESTEROL: return CHOLESTEROL.name().toLowerCase();
            case SYSTOLIC_BLOOD_PRESSURE: return SYSTOLIC_BLOOD_PRESSURE.name().toLowerCase();
            case DIASTOLIC_BLOOD_PRESSURE: return DIASTOLIC_BLOOD_PRESSURE.name().toLowerCase();
            default: throw new IllegalArgumentException();
        }
    }
}
