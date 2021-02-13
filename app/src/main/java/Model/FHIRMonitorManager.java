package Model;

import java.util.List;

/**
 * Is the Model in the MVC Architecture.
 */
public interface FHIRMonitorManager extends FHIRMonitor {

    /**
     * Sets the specific practitioner using the ID and caches them by setting them as the practitioner for this system.
     * @param practitionerId
     * @return
     */
    boolean setPractitioner(String practitionerId);

    /**
     * Retrieves the patients and caches them by setting them as the patients for this system.
     * @return
     */
    boolean setPatients();

    /**
     * Retrieves the patient's condition data and caches them in the patients it already has.
     * @param ids
     * @param type
     * @return
     */
    boolean setMonitor(List<String> ids, List<ObservationType> type);

    /**
     * Retrieves the patient's condition data and updates the cache of the patients it already has.
     * @return
     */
    boolean refreshMonitor();

    void setThreshold(ObservationType type, double value);
}
