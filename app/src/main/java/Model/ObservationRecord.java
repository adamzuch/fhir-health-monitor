package Model;

import java.util.ArrayList;
import java.util.List;

public class ObservationRecord {
    private final ObservationType type;
    private String unit;
    private List<Observation> history;

    public ObservationRecord(ObservationType type) {
        this.type = type;
        this.history = new ArrayList<>();
    }

    public ObservationType getType() {
        return type;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return this.unit;
    }

    public Observation getLatest() {
        return getObservation(0);
    }

    public Observation getObservation(int i) {
        return history.get(i);
    }

    public int getNumberOfObservations(){ return  history.size();}

    public void addObservation(Observation observation) {
        history.add(observation);
    }

    public boolean hasObservation() {
        return hasObservation(0);
    }

    public boolean hasObservation(int i) {
        try {
            return history.get(i) != null;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}
