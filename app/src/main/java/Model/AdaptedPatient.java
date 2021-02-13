package Model;

import org.hl7.fhir.r4.model.Patient;

import java.util.EnumMap;
import java.util.Map;

/**
 * Is the Adapter for the Patient Class from the FHIR Package.
 */
public class AdaptedPatient implements MonitoredPatient {

    private Patient patient;
    private Map<ObservationType, ObservationRecord> records;

    public AdaptedPatient(Patient patient) {
        this.patient = patient;
        this.records = new EnumMap<>(ObservationType.class);
    }

    @Override
    public ObservationRecord getRecord(ObservationType type) {
        return records.get(type);
    }

    @Override
    public void setRecord(ObservationRecord record) {
        records.put(record.getType(), record);
    }

    @Override
    public boolean hasRecord(ObservationType type) {
        return records.get(type) != null;
    }

    @Override
    public String getId() {
        return this.patient.getIdElement().getIdPart();
    }

    @Override
    public String getFullName() {
        return patient.getName().get(0).getGivenAsSingleString() + " " + patient.getName().get(0).getFamily();
    }

    @Override
    public String getDateOfBirth() {
        return patient.getBirthDate().toString();
    }

    @Override
    public String getPatientAddress(){
        String state = patient.getAddressFirstRep().getState();
        String country = patient.getAddressFirstRep().getCountry();
        String city = patient.getAddressFirstRep().getCity();
        return city + ", " + state + ", " + country;
    }

    @Override
    public String getPatientGender(){
        return patient.getGender().getDisplay();
    }
}
