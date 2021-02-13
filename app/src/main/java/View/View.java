package View;

import Controller.Controller;
import Model.ObservationType;
import Model.MonitoredPatient;
import Model.FHIRMonitor;

import java.util.List;

/**
 * View part of the MVC architecture.
 */
public interface View {

    /**
     * Allow the view to indicate that it is in the process of loading.
     */
    void showLoading();

    /**
     * Disable the input after the ID has been entered into the login text bar
     */
    void disableLoginInput();

    /**
     * Change the panel to show viewing the patients
     */
    void enableViewPatients();

    void enableOpenMonitor(boolean enabled);

    /**
     * Enable or disable the refreshing of the patient's data.
     * @param enabled
     */
    void enableStartRefresh(boolean enabled);


    /**
     * Switch the view to the Menu Page after the practitoner ID has been identified and the controller indicates
     * that it is a success (True)
     * @param success
     */
    void loginComplete(boolean success);

    /**
     * If retrieving the patients has been a success then show view which shows patients.
     * @param success
     */
    void patientRetrievalComplete(boolean success);

    /**
     * If retrieving the specific conditional health data has been a success then show the view of health data.
     * @param success
     */
    void observationRetrievalComplete(boolean success);

    /**
     * Called upon by the controller to indicate that retrieving the patient data has been completed and it is once
     * again able to show the view containing the health data.
     * @param success
     */
    void observationRefreshComplete(boolean success);

    /**
     * Reverts back the pane which has a table of patients to choose from such that patients can be added/ removed
     * when viewing their health data.
     */
    void addRemovePatients();

    /**
     * Add listeners to all the views such that this may be relayed to the controller which then decides to
     * take further action.
     * @param controller
     */
    void addActionListeners(Controller controller);

    void addThresholdListeners(Controller controller);

    /**
     * The initial method called upon to show the main window to the user.
     */
    void display();

    void refreshMonitorView(List<MonitoredPatient> patients, List<ObservationType> types);

    void setMonitor(FHIRMonitor monitor);
}
