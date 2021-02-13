package Model;

import java.util.Map;
import java.util.List;

/**
 * The interface for a client to an FHIR server.
 */
public interface FHIRClient {

    /**
     * Validates as to whether the ID is on the FHIR server
     * @param id: ID of practitioner
     * @return: True or false depending of the ID can be found.
     */
    boolean validateId(String id);

    /**
     * Retrieve a specific practitioner, adapting the object into a Practitioner Interface and returning the result.
     * @param id
     * @return: Specific Practitioner Interface.
     */
    MonitoredPractitioner retrievePractitioner(String id);

    /**
     * Received all the patients specific to a certain Practitioner
     * @param practitioner
     * @return: HashMap of IDs linking to the specific patient.
     */
    Map<String, MonitoredPatient> retrievePatients(MonitoredPractitioner practitioner);

    /**
     * Retrieve the patient's condition details for the specific condition that is specified
     * @param ids
     * @param type
     * @return HashMap of patient IDs linking to the specific patient's condition data
     */
    List<Map<String, String>> retrieveObservations(List<String> ids, ObservationType type);
}
