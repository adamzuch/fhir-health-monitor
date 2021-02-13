package Model;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The concrete implementation of the FHIR Client.
 */
public class HAPIClient implements FHIRClient {

    private FhirContext ctx;
    private IGenericClient client;
    private String monashBaseUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir";
    private String flaskBaseUrl = "https://fit3077-flask-backend.azurewebsites.net/";

    public HAPIClient() {
        ctx = FhirContext.forR4();
        ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        ctx.getRestfulClientFactory().setSocketTimeout(200 * 1000);
        ctx.getRestfulClientFactory().setConnectTimeout(200 * 1000);
        client = ctx.newRestfulGenericClient(monashBaseUrl);
    }

    private Bundle searchPractitioner(String id) {
        Bundle bundle = client.search()
            .forResource(Practitioner.class)
            .where(Practitioner.IDENTIFIER.exactly().systemAndCode("http://hl7.org/fhir/sid/us-npi", id))
            .returnBundle(Bundle.class)
            .execute();
        return bundle;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateId(String id) {
        return searchPractitioner(id).hasEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MonitoredPractitioner retrievePractitioner(String id) {
        return new AdaptedPractitioner((Practitioner) searchPractitioner(id).getEntry().get(0).getResource());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, MonitoredPatient> retrievePatients(MonitoredPractitioner practitioner) {
        HashMap<String, MonitoredPatient> patients = new HashMap<>();
        String id = practitioner.getId();
        String practitionerLastName = practitioner.getLastName();
        String url = flaskBaseUrl + "/associatedPatients?pracid=" + id + "&praclname=" + practitionerLastName;
        JSONReader reader = new JSONReader(url);
        Map<String, Object> map = reader.getMap();

        List<String> patientIdList = (ArrayList) map.get("array");

        MonitoredPatient p;
        for (String patientId : patientIdList) {
            System.out.println(patientId);
            p = new AdaptedPatient(client.read().resource(Patient.class).withId(patientId).execute());
            patients.put(patientId, p);
        }
        return patients;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public List<Map<String, String>> retrieveObservations(List<String> patientIds, ObservationType type) {
        StringBuilder patientIdsUrl = new StringBuilder("[");
        for (int i = 0; i < patientIds.size(); i++) {
            if (i == 0) {
                patientIdsUrl.append("%20").append(patientIds.get(i));
            } else {
                patientIdsUrl.append(",%20").append(patientIds.get(i));
            }
        }
        patientIdsUrl.append("]");
        String url;
        if (type.equals(ObservationType.SYSTOLIC_BLOOD_PRESSURE)) {
            url = flaskBaseUrl + "/patientData?patientidarray=" + patientIdsUrl + "&dataArray=[(%27" + type + "%27,5)]";
        } else {
            url = flaskBaseUrl + "/patientData?patientidarray=" + patientIdsUrl + "&dataArray=[(%27" + type + "%27,1)]";
        }

        System.out.println(url);
        JSONReader reader = new JSONReader(url);
        Map<String, Object> map = reader.getMap();
        List<Map<String, String>> dataList = (ArrayList<Map<String, String>>) map.get("array");
        System.out.println(dataList);


        return dataList;
    }
}
