package Model;

import java.util.List;
import java.util.Map;

/**
 * Is the Read only Model that the View can access since the view should not be able to change the model
 * (that should only be done by the controller) in MVC Architecture.
 */
public interface FHIRMonitor {

    /**
     * Retrieves the specific practitioner that has previously been set for the system.
     * @return
     */
    MonitoredPractitioner getPractitioner();

    /**
     * Returns all the patients in the form of a Map.
     * @return
     */
    Map<String, MonitoredPatient> getPatients();

    /**
     * Packages only the monitored patients and returns them in a Map.
     * @return
     */
    List<MonitoredPatient> getMonitoredPatients();


    List<ObservationType> getMonitoredTypes();
}
