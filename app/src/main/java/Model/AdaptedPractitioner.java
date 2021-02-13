package Model;

import org.hl7.fhir.r4.model.Practitioner;

/**
 * Is the Adapter for the Practitioner Class from the FHIR Package.
 */
public class AdaptedPractitioner implements MonitoredPractitioner {

    Practitioner practitioner;

    public AdaptedPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
    }

    @Override
    public String getId() {
        return practitioner.getIdentifier().get(0).getValue();
    }

    @Override
    public String getFullName() {
        return practitioner.getName().get(0).getGivenAsSingleString()+" "+practitioner.getName().get(0).getFamily();
    }

    @Override
    public String getLastName() {
        return practitioner.getName().get(0).getFamily();
    }
}
