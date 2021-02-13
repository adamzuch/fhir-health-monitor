package View;

import Model.ObservationType;
import Model.MonitoredPatient;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The panel that shows All the patients for a specific practitioner.
 */
public class PatientsPane extends JPanel {

    private JCheckBox cholesterolButton;
    private JCheckBox systolicButton;
    private JCheckBox diastolicButton;

    private static JTable patientTable;
    private JButton openMonitorButton;

    public PatientsPane(){
        initComponents();
    }

    /**
     * Shows the patients in a table.
     */
    public void showPatients(Map<String, MonitoredPatient> patients){
        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        for (Map.Entry mapElement : patients.entrySet()) {
            String id = (String) mapElement.getKey();
            MonitoredPatient patient = patients.get(id);
            model.addRow(new Object[]{false, id, patient.getFullName()});
        }
    }

    /**
     * Initialises the components for its view.
     */
    private void initComponents() {
        // add table which will contain all patients
        patientTable = new JTable();
        patientTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"SELECT PATIENT", "ID", "NAME"}) {
            Class[] types = new Class [] {
                    java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                    true, false, false
            };
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });

        // add the scroll pane to this panel
        JScrollPane scrollPane = new JScrollPane(patientTable);
        this.add(scrollPane);

        // add buttons for all observations
        cholesterolButton = new JCheckBox();
        systolicButton = new JCheckBox();
        diastolicButton = new JCheckBox();
        cholesterolButton.setText(ObservationType.CHOLESTEROL.toString());
        systolicButton.setText(ObservationType.SYSTOLIC_BLOOD_PRESSURE.toString());
        diastolicButton.setText(ObservationType.DIASTOLIC_BLOOD_PRESSURE.toString());

        // add panel which contains observations to be selected
        JPanel observationsPanel = new JPanel(new GridLayout(3, 0));
        observationsPanel.setBorder(BorderFactory.createTitledBorder("Observations to Monitor"));
        observationsPanel.add(cholesterolButton);
        observationsPanel.add(systolicButton);
        observationsPanel.add(diastolicButton);
        this.add(observationsPanel);

        // add in the button for opening the monitor
        openMonitorButton = new JButton();
        openMonitorButton.setText("Open Monitor");
        this.add(openMonitorButton);
    }

    public List<ObservationType> getSelectedObservations() {
        List<ObservationType> selected = new ArrayList<>();
        if (cholesterolButton.isSelected()) selected.add(ObservationType.CHOLESTEROL);
        if (systolicButton.isSelected()) selected.add(ObservationType.SYSTOLIC_BLOOD_PRESSURE);
        if (diastolicButton.isSelected()) selected.add(ObservationType.DIASTOLIC_BLOOD_PRESSURE);
        return selected;
    }

    /**
     * Returns a List of IDs of the patients which have been selected.
     * @return An Array of IDs
     */
    public List<String> getSelectedPatients(){
        List<String> selectedPatients = new ArrayList<>();
        for (int row = 0; row < patientTable.getRowCount(); row++){
            DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
            boolean selected = (boolean) model.getValueAt(row, 0);
            // if they have been selected then add them to the hashmap
            if (selected) {
                // at row 2 the ID of the patient exists
                String patientID = (String) model.getValueAt(row, 1);
                selectedPatients.add(patientID);
            }
        }
        return selectedPatients;
    }

    public JButton getOpenMonitorButton() {
        return openMonitorButton;
    }
}
