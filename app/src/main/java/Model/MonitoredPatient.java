package Model;

/**
 * An interface for interacting with the PatientAdapter.
 */
public interface MonitoredPatient {

    ObservationRecord getRecord(ObservationType type);
    void setRecord(ObservationRecord record);
    boolean hasRecord(ObservationType type);
    String getId();
    String getFullName();
    String getDateOfBirth();
    String getPatientAddress();
    String getPatientGender();
}
