package View;

import Controller.Controller;
import Model.ObservationType;
import Model.MonitoredPatient;
import Model.FHIRMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * The Main window of the entire system, which is in charge of changing its panels to support different views.
 */
public class MainWindow extends JFrame implements View {

    private final String WINDOW_TITLE = "FIT3077 Health Monitor";
    private final String LOGIN_PANE_NAME = "login";
    private final String MENU_PANE_NAME = "menu";
    private final String PATIENTS_PANE_NAME = "patients";
    private final String MONITOR_PANE_NAME = "monitor";

    private CardLayout layout;
    private JPanel panels;

    private LoginPane loginPane;
    private MenuPane menuPane;
    private PatientsPane patientsPane;
    private MonitorPane monitorPane;

    private FHIRMonitor monitor;

    public MainWindow() {
        createPanels();
        createWindow();
    }

    /**
     * Initialises the necessary panels for the entire application.
     */
    private void createPanels() {
        panels = new JPanel();
        layout = new CardLayout();

        panels.setLayout(layout);

        loginPane = new LoginPane();
        menuPane = new MenuPane();
        patientsPane = new PatientsPane();
        monitorPane = new MonitorPane();

        panels.add(loginPane, LOGIN_PANE_NAME);
        panels.add(menuPane, MENU_PANE_NAME);
        panels.add(patientsPane, PATIENTS_PANE_NAME);
        panels.add(monitorPane, MONITOR_PANE_NAME);
    }

    /**
     * Creates the Initial Window
     */
    private void createWindow() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle(WINDOW_TITLE);
        this.add(panels);
    }

    @Override
    public void addActionListeners(Controller controller) {
        // listeners for login pane
        loginPane.getEnterButton().addActionListener(e -> controller.requestLogin(loginPane.getIdInput().getText()));
        loginPane.getIdInput().addActionListener(e -> controller.requestLogin(loginPane.getIdInput().getText()));  // if enter key pressed.

        // listeners for menu pane
        menuPane.getViewPatientsButton().addActionListener(e -> controller.requestViewPatients());

        // listeners for patients pane
        patientsPane.getOpenMonitorButton().addActionListener(e -> {
            List<String> ids = patientsPane.getSelectedPatients();
            List<ObservationType> types = patientsPane.getSelectedObservations();
            if (ids.isEmpty() || types.isEmpty()) {
                JOptionPane.showMessageDialog(patientsPane, "At least one patient and observation must be selected to monitor");
            } else {
                controller.requestMonitor(ids, types);
            }
        });

        // listeners for monitor pane
        monitorPane.getAddRemovePatientsButton().addActionListener(e -> controller.requestAddRemovePatientsFromMonitor());
        //listeners for the  refreshing panel
        monitorPane.getStartRefreshButton().addActionListener(e -> controller.refreshMonitor(getRefreshTime()));
        monitorPane.getCancelRefreshButton().addActionListener(e -> controller.cancelRefreshMonitor());
        monitorPane.getRefreshTextField().addActionListener(e -> controller.refreshMonitor(getRefreshTime()));

        //listener for the patient details once clicked on table
        monitorPane.getPatientTable().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                MainWindow.this.showPatientDetailsWindow(evt);
            }
        });
    }

    @Override
    public void addThresholdListeners(Controller controller) {
        //First add the listeners to the monitor pane
        monitorPane.showThresholdControlPanels(patientsPane.getSelectedObservations());

        //Now add the listeners
        //listeners for the systolic threshold
        if(monitorPane.getSetSystolicThresholdButton() != null) {
            monitorPane.getSetSystolicThresholdButton().addActionListener(e -> controller.setThreshold(ObservationType.SYSTOLIC_BLOOD_PRESSURE, getThresholdValue(ObservationType.SYSTOLIC_BLOOD_PRESSURE)));
            monitorPane.getSystolicThresholdTextField().addActionListener(e -> controller.setThreshold(ObservationType.SYSTOLIC_BLOOD_PRESSURE, getThresholdValue(ObservationType.SYSTOLIC_BLOOD_PRESSURE)));
        }
        //listeners for the diastolic threshold
        if(monitorPane.getSetDiastolicThresholdButton() != null) {
            monitorPane.getSetDiastolicThresholdButton().addActionListener(e -> controller.setThreshold(ObservationType.DIASTOLIC_BLOOD_PRESSURE, getThresholdValue(ObservationType.DIASTOLIC_BLOOD_PRESSURE)));
            monitorPane.getDiastolicThresholdTextField().addActionListener(e -> controller.setThreshold(ObservationType.DIASTOLIC_BLOOD_PRESSURE, getThresholdValue(ObservationType.DIASTOLIC_BLOOD_PRESSURE)));
        }
    }

    /**
     * Show the details of a specific patient once clicked.
     * @param evt
     */
    private void showPatientDetailsWindow(java.awt.event.MouseEvent evt){
        JTable source = (JTable) evt.getSource();
        int row = source.rowAtPoint(evt.getPoint());

        // get the column containing the id of the patient
        int id_column = 0;
        String id = (String) source.getModel().getValueAt(row, id_column);

        // find the patient that has this ID
        MonitoredPatient patient = monitor.getPatients().get(id);
        System.out.println(patient);

        // start a new window which shows patient details
        PatientDetailsWindow pdw = new PatientDetailsWindow(patient);
        pdw.pack();
        pdw.setLocationRelativeTo(null);
        pdw.setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showLoading() {
        loginPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loginComplete(boolean success) {
        if (success) {
            menuPane.getPractitionerNameLabel().setText(monitor.getPractitioner().getFullName());
            layout.show(panels, MENU_PANE_NAME);
        } else {
            loginPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(loginPane,"Invalid ID entered or server connection timed out");
            loginPane.getEnterButton().setEnabled(true);
            loginPane.getIdInput().setEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void patientRetrievalComplete(boolean success) {
        if (success) {
            patientsPane.showPatients(monitor.getPatients());
            layout.show(panels, PATIENTS_PANE_NAME);
        } else {
            JOptionPane.showMessageDialog(menuPane,"Error retrieving patients");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void observationRetrievalComplete(boolean success) {
        if (success) {
            monitorPane.refresh(monitor.getMonitoredPatients(), monitor.getMonitoredTypes());
            layout.show(panels, MONITOR_PANE_NAME);
            this.enableStartRefresh(true);
        } else {
            JOptionPane.showMessageDialog(patientsPane,"Error retrieving observations");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void observationRefreshComplete(boolean success) {
        if (success) {
            monitorPane.refresh(monitor.getMonitoredPatients(), monitor.getMonitoredTypes());
        } else {
            JOptionPane.showMessageDialog(patientsPane,"Error retrieving observations");
        }
    }

    @Override
    public void addRemovePatients() {
        this.enableOpenMonitor(true);
        layout.show(panels, PATIENTS_PANE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableLoginInput() {
        loginPane.getEnterButton().setEnabled(false);
        loginPane.getIdInput().setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableViewPatients() {
        menuPane.getViewPatientsButton().setEnabled(true);
    }

    @Override
    public void enableOpenMonitor(boolean enabled) {
        patientsPane.getOpenMonitorButton().setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableStartRefresh(boolean enabled) {
        monitorPane.getRefreshTextField().setEnabled(enabled);
        monitorPane.getStartRefreshButton().setEnabled(enabled);
        monitorPane.getCancelRefreshButton().setEnabled(!enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void display() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void refreshMonitorView(List<MonitoredPatient> patients, List<ObservationType> types) {
        monitorPane.refresh(patients, types);
    }

    /**
     *  Gets the refresh time from the monitor pane view.
     */
    private int getRefreshTime() {
        String refreshTime = monitorPane.getRefreshTextField().getText();
        // remove all whitespaces
        refreshTime = refreshTime.replaceAll("\\s","");
        try {
            return Integer.parseInt(refreshTime);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(monitorPane,"Refresh time must be a positive integer");
        }
        // otherwise return 0 seconds
        return 0;
    }

    private double getThresholdValue(ObservationType observationType){
        //return the threshold value based on the observation type
        String thresholdValue = null;
        if (observationType.equals(ObservationType.DIASTOLIC_BLOOD_PRESSURE)){
            thresholdValue = monitorPane.getDiastolicThresholdTextField().getText();
        }
        else if(observationType.equals(ObservationType.SYSTOLIC_BLOOD_PRESSURE)){
            thresholdValue = monitorPane.getSystolicThresholdTextField().getText();
        }

        //remove all whitespaces
        assert thresholdValue != null;
        thresholdValue = thresholdValue.replaceAll("\\s","");

        //Try and return the integer value of this threshold
        try {
            return Double.parseDouble(thresholdValue);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(monitorPane,"Threshold value must be a positive integer");
        }

        //Otherwise return a null value that wont update the threshold value
        return -1.0;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMonitor(FHIRMonitor monitor) {
        this.monitor = monitor;
    }
}
