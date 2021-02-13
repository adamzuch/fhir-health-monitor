package Model;

import java.util.*;

/**
 * Concrete implementation of the Model.
 */
public class MonitorManager implements FHIRMonitorManager {

    private FHIRClient client;
    private MonitoredPractitioner practitioner;
    private Map<String, MonitoredPatient> patients;
    private List<String> monitoredIds;
    private List<ObservationType> monitoredTypes;
    private Map<ObservationType, Double> thresholds;

    public MonitorManager(FHIRClient client) {
        this.client = client;
        this.thresholds = new EnumMap<>(ObservationType.class);

        // set default thresholds for high measurements
        for (ObservationType type : ObservationType.values()) {
            this.thresholds.put(type, type.getDefaultThresholdValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MonitoredPractitioner getPractitioner() {
        return practitioner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, MonitoredPatient> getPatients() {
        return new HashMap<>(this.patients);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MonitoredPatient> getMonitoredPatients() {
        List<MonitoredPatient> monitoredPatients = new ArrayList<>();
        for (String id : this.monitoredIds) {
            monitoredPatients.add(this.patients.get(id));
        }
        return monitoredPatients;
    }

    @Override
    public List<ObservationType> getMonitoredTypes() {
        return monitoredTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setPractitioner(String practitionerId) {
        if (!practitionerId.isEmpty() && client.validateId(practitionerId)) {
            this.practitioner = client.retrievePractitioner(practitionerId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setPatients() {
        try {
            patients = client.retrievePatients(this.getPractitioner());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setMonitor(List<String> ids, List<ObservationType> types) {
        monitoredIds = ids;
        monitoredTypes = types;
        return refreshMonitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean refreshMonitor() {
        List<Map<String, String>> data;
        MonitoredPatient patient;
        ObservationRecord record;
        String id;
        String value;
        String unit;
        String time;
        int numberOfEntries;

        for (ObservationType type: monitoredTypes) {
            data = client.retrieveObservations(monitoredIds, type);
            try {
                for (Map<String, String> d : data) {
                    id = d.get("ID");
                    unit = d.get(type + "_units");
                    patient = patients.get(id);

                    // create a new record if it does not yet exist
                    if (!patient.hasRecord(type)) {
                        patient.setRecord(new ObservationRecord(type));
                        patient.getRecord(type).setUnit(unit);
                    }
                    record = patient.getRecord(type);
                    numberOfEntries = (d.entrySet().size() - 2) / 2;

                    // fill record with observation data
                    for (int i = 0; i < numberOfEntries; i++) {
                        value = d.get(type + "_data_" + i);
                        time = d.get(type + "_timeIssued_" + i);
                        System.out.println(value + ", " + time);
                        // update observation if it exists
                        if (record.hasObservation(i)) {
                            record.getObservation(i).setValue(value);
                            record.getObservation(i).setTime(time);
                        } else {
                            record.addObservation(new Observation(value, time));
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }
            // after refresh need to update the threshold for cholesterol values because they are based off average
            if (type.equals(ObservationType.CHOLESTEROL)) {
                setThreshold(type, getAverageMonitoredValue(type));
            } else {
                flagValuesAboveThreshold(type);
            }
        }
        return true;
    }

    public void setThreshold(ObservationType type, double value) {
        thresholds.put(type, value);
        // call upon the method to update the threshold for all values
        flagValuesAboveThreshold(type);
    }

    private void flagValuesAboveThreshold(ObservationType type) {
        MonitoredPatient patient;
        ObservationRecord record;
        double value;
        // iterate through all monitored patients and for each monitored observation check whether value exceeds designated threshold.
        for (String id : monitoredIds) {
            patient = patients.get(id);

            // check if the patient has the record type
            if (patient.hasRecord(type)){
                record = patient.getRecord(type);
                for (int i = 0; record.hasObservation(i); i++) {
                    value = Double.parseDouble(record.getObservation(i).getValue());
                    record.getObservation(i).setAboveThreshold(value > thresholds.get(type));
                }
            }
        }
    }

    private double getAverageMonitoredValue(ObservationType type) {
        double sum = 0.0;
        //Not all monitor IDS have the cholesterol value
        int numberOfValues = monitoredIds.size();
        for (String id : monitoredIds) {
            //Check if the patient has the type of observation
            if (patients.get(id).hasRecord(type)) {
                if (patients.get(id).getRecord(type).hasObservation()) {
                    sum += Double.parseDouble(patients.get(id).getRecord(type).getLatest().getValue());
                } else {
                    numberOfValues--;
                }
            }
            else {
                numberOfValues--;
            }
        }
        if (numberOfValues > 0) {
            return sum / numberOfValues;
        } else {
            return 0.0;
        }
    }
}
