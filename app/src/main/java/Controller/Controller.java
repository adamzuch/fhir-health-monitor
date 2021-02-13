package Controller;

import Model.ObservationType;

import java.util.List;

/**
 * The controller of the entire system acting as the intermediary between the model and the view.
 */
public interface Controller {

    /**
     * Requests the model as to whether the login was successful to relay onto the view.
     * @param userId
     */
    void requestLogin(String userId);

    /**
     * Requests the model to retrieve all the patients from the server such that this is then relayed to the view.
     */
    void requestViewPatients();

    /**
     *Requests the model to retrieve all the patient data from the server and then initiate the monitor view
     */
    void requestMonitor(List<String> patientIdList, List<ObservationType> type);

    /**
     *Once refresh is begun, it requests the model to retrieve all the patient data from the server and then initiate the monitor view
     */
    void refreshMonitor(int refreshTime);

    /**
     * Halts the refreshing of patient data
     */
    void cancelRefreshMonitor();

    /**
     * Initiates the view to change such that adding/removing patients become viable.
     */
    void requestAddRemovePatientsFromMonitor();

    /**
     * Sets the threshold of the systolic in the model
     */
    void setThreshold(ObservationType observationType, Double threshold);

}
