package Controller;

import Model.ObservationType;
import Model.FHIRMonitorManager;
import View.View;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Concrete Implementation of the controller.
 */
public class SwingController implements Controller {

    private View view;
    private FHIRMonitorManager manager;
    private SwingWorker worker;
    private boolean patientRetrievalSuccess;
    private int dataRefreshTime;
    private boolean dataRefreshActivated = false;

    public SwingController(View view, FHIRMonitorManager manager) {
        this.view = view;
        view.addActionListeners(this);
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestLogin(String userId) {
        view.disableLoginInput();
        view.showLoading();
        this.worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return manager.setPractitioner(userId);
            }
            @Override
            protected void done() {
                boolean loginSuccess = false;
                try {
                    loginSuccess = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.getCause().printStackTrace();
                }
                if (loginSuccess) {
                    SwingController.this.requestPatients();
                }
                view.loginComplete(loginSuccess);
            }
        };
        this.worker.execute();
    }

    /**
     * Intitiates the model to request and set patients from the server.
     */
    private void requestPatients() {
        this.worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return manager.setPatients();
            }
            @Override
            protected void done() {
                try {
                    SwingController.this.patientRetrievalSuccess = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.getCause().printStackTrace();
                }
                view.enableViewPatients();
            }
        };
        this.worker.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestViewPatients() {
        view.patientRetrievalComplete(patientRetrievalSuccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestMonitor(List<String> ids, List<ObservationType> types) {
        view.enableOpenMonitor(false);
        this.worker = new SwingWorker<Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground() {
                return manager.setMonitor(ids, types);
            }
            @Override
            protected void done() {
                boolean healthDataRetrievalSuccess = false;
                try {
                    healthDataRetrievalSuccess = get();
                } catch (InterruptedException | ExecutionException e) {
                    e.getCause().printStackTrace();
                }
                view.observationRetrievalComplete(healthDataRetrievalSuccess);
            }
        };
        this.worker.execute();

        //Add in the action listeners for the threshold of different observations
        this.view.addThresholdListeners(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshMonitor(int dataRefreshTime) {
        view.enableStartRefresh(false);
        this.dataRefreshTime = dataRefreshTime;
        // only start refreshing if it is deactivated
        if (!dataRefreshActivated) {
            dataRefreshActivated = true;
            this.worker = new SwingWorker<Boolean, Boolean>() {
                @Override
                protected Boolean doInBackground() {
                    boolean result = false;
                    while (dataRefreshActivated) {
                        int n = SwingController.this.dataRefreshTime * 1000;  // convert to milliseconds
                        // can't wait negative time
                        if (n < 0) n = 0;
                        try {
                            Thread.sleep(n);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (dataRefreshActivated) result = manager.refreshMonitor();
                        if (dataRefreshActivated) publish(result);
                    }
                    return result;
                }
                @Override
                protected void process(List<Boolean> result) {
                    view.observationRefreshComplete(result.get(0));
                }
            };
            this.worker.execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelRefreshMonitor() {
        dataRefreshActivated = false;
        view.enableStartRefresh(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestAddRemovePatientsFromMonitor() {
        this.cancelRefreshMonitor();
        view.addRemovePatients();
    }

    @Override
    public void setThreshold(ObservationType observationType, Double threshold) {

        //If  the threshold number is not -1.0 then update the model
        if(!threshold.equals(-1.0)) {
            manager.setThreshold(observationType, threshold);
            view.refreshMonitorView(manager.getMonitoredPatients(), manager.getMonitoredTypes());
        }
    }
}
