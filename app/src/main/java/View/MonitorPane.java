package View;

import Model.Observation;
import Model.ObservationRecord;
import Model.ObservationType;
import Model.MonitoredPatient;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The Monitor Pane which monitors a specific kind of condition.
 */
public class MonitorPane extends JPanel {

    private final String CHOLESTEROL_CHART_TAB_NAME = "Cholesterol Chart";
    private final String SYSTOLIC_TEXT_TAB_NAME = "Systolic Text";
    private final String SYSTOLIC_CHART_TAB_NAME = "Systolic Chart";
    private JTabbedPane tabbedPane;

    // control panel
    private JPanel controlPanel;

    // refresh data control panel
    private JButton addRemovePatientsButton;
    private JButton startRefreshButton;
    private JButton cancelRefreshButton;
    private JTextField refreshTextField;

    // systolic threshold panel
    private JPanel systolicControlPanel;
    private JButton setSystolicThresholdButton;
    private JTextField systolicThresholdTextField;

    // diastolic threshold panel
    private JPanel diastolicControlPanel;
    private JButton setDiastolicThresholdButton;
    private JTextField diastolicThresholdTextField;

    // patient table
    private JTable patientTable;

    // textual monitor for systolic blood pressure history
    private JTextArea systolicTextArea;

    // charts for cholesterol and systolic blood pressure history
    private MonitorChart cholesterolChart;
    private MonitorChart systolicChart;

    public MonitorPane(){
        initComponents();
    }

    /**
     * Initialises the components for its view.
     */
    private void initComponents(){
        //Initiate new tabbed pane
        this.tabbedPane = new JTabbedPane();
        patientTable = new JTable();

        patientTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"ID", "NAME",}) {
            Class[] types = new Class[] {
                    java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class ,java.lang.Object.class,java.lang.Object.class ,java.lang.Object.class
            };
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false, false
            };
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        //Add the table to the scroll pane
        JScrollPane patientTableScrollPane = new JScrollPane(patientTable);

        //Add the scrolled pane to a tabbed pane
        tabbedPane.addTab("Table", patientTableScrollPane);

        // initialize chart panel and bar chart for cholesterol
        JFreeChart chart = ChartFactory.createBarChart("", "", "", null, PlotOrientation.VERTICAL, false, false, false);
        this.cholesterolChart = new MonitorChart(chart);
        ChartPanel chartPanel = new ChartPanel(this.cholesterolChart.getChart());
        chartPanel.setPreferredSize(new Dimension(250, 250));

        // add the cholesterol chart panel to a new tab
        tabbedPane.addTab(CHOLESTEROL_CHART_TAB_NAME, chartPanel);

        // initialize text area for systolic blood pressure history
        this.systolicTextArea = new JTextArea(2, 20);
        systolicTextArea.setLineWrap(true);
        systolicTextArea.setWrapStyleWord(true);
        // add the systolic textual monitor to a new tab
        tabbedPane.addTab(SYSTOLIC_TEXT_TAB_NAME, new JScrollPane(systolicTextArea));

        // initialize systolic blood pressure chart panel and line chart
        chart = ChartFactory.createLineChart("", "Patients", "", null, PlotOrientation.VERTICAL, true, false, false);
        this.systolicChart = new MonitorChart(chart);
        chartPanel = new ChartPanel(this.systolicChart.getChart());
        chartPanel.setPreferredSize(new Dimension(250, 250));

        // add the systolic chart to a new tab
        tabbedPane.add(SYSTOLIC_CHART_TAB_NAME, new JScrollPane(chartPanel));

        //Add a control panel to house the buttons
        this.controlPanel = new JPanel();
        //Make the control panel to be of a box layout
        this.controlPanel.setLayout(new BoxLayout(this.controlPanel, BoxLayout.Y_AXIS));

        //Add a panel to have the refreshing buttons
        // refresh data control panel
        JPanel refreshControlPanel = new JPanel();
        // add in the Add/Remove button to the control panel
        this.addRemovePatientsButton = new JButton();
        this.addRemovePatientsButton.setText("Add/Remove Patients");
        refreshControlPanel.add(addRemovePatientsButton);
        // add in the start refresh button
        this.startRefreshButton = new JButton();
        this.startRefreshButton.setText("Start Refresh");
        refreshControlPanel.add(this.startRefreshButton);
        // add in the cancel refresh button
        this.cancelRefreshButton = new JButton();
        this.cancelRefreshButton.setText("Cancel Refresh");
        refreshControlPanel.add(this.cancelRefreshButton);
        // add in a text field
        refreshTextField = new JTextField(3);
        refreshControlPanel.add(refreshTextField);
        // add a label for the refresh text field
        JLabel refreshTextLabel = new JLabel("Refresh Time");
        refreshTextLabel.setLabelFor(refreshTextField);
        refreshControlPanel.add(refreshTextLabel);
        //Add the refresh panel to the control panel
        this.controlPanel.add(refreshControlPanel);



        // Set the layout of the monitor panel to be box layout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Add the tabbed pane to the monitor panel
        this.add(tabbedPane);
        //Add the control panel to the monitor panel
        this.add(controlPanel);
    }

    public void refresh(List<MonitoredPatient> patients, List<ObservationType> types) {
        showTabs(types);
        generateTable(patients, types);
        // update the graph with cholesterol data
        if (types.contains(ObservationType.CHOLESTEROL)) {
            generateCholesterolChart(patients);
        }
        if (types.contains(ObservationType.SYSTOLIC_BLOOD_PRESSURE)) {
            this.generateSystolicText(patients);
            this.generateSystolicChart(patients);
        }
        highlightPatients(patients, types);
    }

    /**
     * Shows the patients in a table along with the data of the condition.
     * @param patients
     */
    private void generateTable(List<MonitoredPatient> patients, List<ObservationType> types) {
        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        // clear the table
        model.setRowCount(0);
        model.setColumnCount(2);

        // based on all the observation types, add a new column for each of them
        for (ObservationType type: types) {
            int rowIndex = 0;
            model.addColumn(type.toString());
            model.addColumn("TIME UPDATED");

            for (MonitoredPatient patient : patients) {
                ObservationRecord record = patient.getRecord(type);
                // only add patients that don't have null health condition data
                if (patient.hasRecord(type)) {
                    String valueAndUnit = record.getLatest().getValue() + record.getUnit();
                    String timeUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    if (rowIndex >= model.getRowCount()) {
                        model.addRow(new Object[]{patient.getId(), patient.getFullName()});
                    }
                    model.setValueAt(valueAndUnit, rowIndex, model.getColumnCount() - 2);
                    model.setValueAt(timeUpdated, rowIndex, model.getColumnCount() - 1);
                } else {
                    //Still need to add a row if patient is missing data, we just exclude putting anything there
                    if (rowIndex >= model.getRowCount()) {
                        model.addRow(new Object[]{patient.getId(), patient.getFullName()});
                    }
                }
                rowIndex++;
            }
        }
    }

    private void generateCholesterolChart(List<MonitoredPatient> patients) {
        ObservationRecord record;
        List<String> names = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        String unit = "";
        ObservationType cholesterol = ObservationType.CHOLESTEROL;

        for (MonitoredPatient patient : patients) {
            // check that this patient has record for CHOLESTEROL
            if (patient.hasRecord(cholesterol)) {
                record = patient.getRecord(cholesterol);
                unit = record.getUnit();

                names.add(patient.getFullName());
                values.add(Double.parseDouble(record.getLatest().getValue()));
            }
        }
        cholesterolChart.getChart().setTitle(cholesterol.toString() + " " + unit);
        cholesterolChart.updateDataset(names, values);
    }

    private void generateSystolicText(List<MonitoredPatient> patients){
        //clear the text area
        this.systolicTextArea.setText("");

        //Iterate through all the patients
        for (MonitoredPatient patient : patients) {

            //Check if patient has a record for
            if (patient.hasRecord(ObservationType.SYSTOLIC_BLOOD_PRESSURE)) {

                StringBuilder strToAppend = new StringBuilder(patient.getFullName()+":");
                ObservationRecord record = patient.getRecord(ObservationType.SYSTOLIC_BLOOD_PRESSURE);

                // Only add if the latest observation is above the threshold
                if(record.getLatest().isAboveThreshold()) {
                    //Iterate through all the records and add it into the text area
                    for (int i = record.getNumberOfObservations() - 1; i >= 0; i--) {
                        strToAppend.append("    ");
                        // TODO: Format the time properly
                        strToAppend.append(record.getObservation(i).getValue()).append(" ").append('(').append(record.getObservation(i).getTime()).append(')').append(',');
                    }
                    strToAppend.append('\n');
                    strToAppend.append('\n');
                    this.systolicTextArea.append(strToAppend.toString());
                }
            }
        }
    }

    private void generateSystolicChart(List<MonitoredPatient> patients) {
        ObservationRecord record;
        String unit = "";
        Observation observation;
        List<String> names = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        ObservationType systolic = ObservationType.SYSTOLIC_BLOOD_PRESSURE;

        for (MonitoredPatient patient : patients) {
            if (patient.hasRecord(systolic)) {
                record = patient.getRecord(systolic);
                unit = record.getUnit();

                // only get patients in which their latest systolic blood pressure reading is high
                if (record.getLatest().isAboveThreshold()) {
                    // iterate backwards to get earlier measurements first
                    int x = 1;
                    for (int i = record.getNumberOfObservations() - 1; i >= 0; i--) {
                        observation = record.getObservation(i);
                        names.add(Integer.toString(x));
                        categories.add(patient.getFullName());
                        values.add(Double.parseDouble(observation.getValue()));
                        x++;
                    }
                }
            }
        }
        this.systolicChart.getChart().setTitle(systolic.toString() + " " + unit);
        this.systolicChart.updateDataset(names, categories, values);
    }

    /**
     * Highlights the patients which have a condition value that is greater than the average.
     */
    private void highlightPatients(List<MonitoredPatient> patients, List<ObservationType> types) {
        List<Integer> chosenRows;

        Map<ObservationType, List<Integer>> rows = new EnumMap<>(ObservationType.class);
        Map<ObservationType, Integer> columns = new EnumMap<>(ObservationType.class);

        for (ObservationType type : types) {
            // find column index of observation type which contains values
            for (int columnIndex = 0; columnIndex < patientTable.getColumnCount(); columnIndex++) {
                if (patientTable.getColumnName(columnIndex).equals(type.toString())) {
                    columns.put(type, columnIndex);
                }
            }
            // find rows which contain values above the threshold
            chosenRows = new ArrayList<>();
            int rowIndex = 0;  // start at 1 because row index for header is 0
            for (MonitoredPatient patient : patients) {
                if (patient.hasRecord(type)) {
                    if (patient.getRecord(type).getLatest().isAboveThreshold()) {
                        chosenRows.add(rowIndex);
                    }
                }
                rowIndex++;
            }
            rows.put(type, chosenRows);
            // use column color renderer to highlight chosen column and rows
            TableColumn tColumn = patientTable.getColumnModel().getColumn(columns.get(type));
            Color textColor;
            if (type.equals(ObservationType.CHOLESTEROL)) {
                textColor = Color.RED;
            } else {
                textColor = Color.BLUE;
            }
            tColumn.setCellRenderer(new ColumnColorRenderer(Color.WHITE, textColor, rows.get(type)));
            patientTable.firePropertyChange("highlightPatients", true, false);
        }
    }

    private void showTabs(List<ObservationType> types) {
        ObservationType cholesterol = ObservationType.CHOLESTEROL;
        ObservationType systolic = ObservationType.SYSTOLIC_BLOOD_PRESSURE;

        tabbedPane.setEnabledAt(tabbedPane.indexOfTab(CHOLESTEROL_CHART_TAB_NAME), types.contains(cholesterol));
        tabbedPane.setEnabledAt(tabbedPane.indexOfTab(SYSTOLIC_CHART_TAB_NAME), types.contains(systolic));
        tabbedPane.setEnabledAt(tabbedPane.indexOfTab(SYSTOLIC_TEXT_TAB_NAME), types.contains(systolic));
    }

    public void showThresholdControlPanels(List<ObservationType> types) {
        // remove all control panels if they exist
        if (this.systolicControlPanel != null){
            this.systolicControlPanel.removeAll();
        }
        if (this.diastolicControlPanel != null ) {
            this.diastolicControlPanel.removeAll();
        }
        ObservationType systolic = ObservationType.SYSTOLIC_BLOOD_PRESSURE;
        ObservationType diastolic = ObservationType.DIASTOLIC_BLOOD_PRESSURE;
        // add the systolic control panel if that was one of the observations chosen
        if (types.contains(systolic)){
            addSystolicControlPanel();
        }
        // add the diastolic control panel if that was one of the observations chosen
        if (types.contains(diastolic)){
            addDiastolicControlPanel();
        }
    }

    private void addSystolicControlPanel(){
        //Add a panel to have the systolic control buttons and input
        this.systolicControlPanel = new JPanel();
        // add in the set threshold button
        this.setSystolicThresholdButton = new JButton();
        this.setSystolicThresholdButton.setText("Set Systolic Threshold");
        this.systolicControlPanel.add(this.setSystolicThresholdButton);
        // add in a text field to enter the threshold
        this.systolicThresholdTextField = new JTextField(3);
        this.systolicControlPanel.add(systolicThresholdTextField);
        // add a label for the refresh text field
        JLabel systolicTextLabel = new JLabel("Systolic Threshold");
        systolicTextLabel.setLabelFor(systolicThresholdTextField);
        this.systolicControlPanel.add(systolicTextLabel);
        //Add the systolic panel to the control panel
        this.controlPanel.add(this.systolicControlPanel);
    }

    private void addDiastolicControlPanel(){
        //Add a panel to have the diastolic control buttons and input
        this.diastolicControlPanel = new JPanel();
        // add in the set threshold button
        this.setDiastolicThresholdButton = new JButton();
        this.setDiastolicThresholdButton.setText("Set Diastolic Threshold");
        this.diastolicControlPanel.add(this.setDiastolicThresholdButton);
        // add in a text field to enter the threshold
        this.diastolicThresholdTextField = new JTextField(3);
        this.diastolicControlPanel.add(diastolicThresholdTextField);
        // add a label for the refresh text field
        JLabel diastolicTextLabel = new JLabel("Diastolic Threshold");
        diastolicTextLabel.setLabelFor(diastolicThresholdTextField);
        this.diastolicControlPanel.add(diastolicTextLabel);
        //Add the systolic panel to the control panel
        this.controlPanel.add(this.diastolicControlPanel);
    }

    public JButton getAddRemovePatientsButton() {
        return addRemovePatientsButton;
    }

    public JButton getStartRefreshButton() {
        return this.startRefreshButton;
    }

    public JButton getCancelRefreshButton() {
        return this.cancelRefreshButton;
    }

    public JTextField getRefreshTextField() {
        return refreshTextField;
    }

    public JButton getSetSystolicThresholdButton(){
        return this.setSystolicThresholdButton;
    }

    public JButton getSetDiastolicThresholdButton(){
        return this.setDiastolicThresholdButton;
    }

    public JTextField getSystolicThresholdTextField(){
        return this.systolicThresholdTextField;
    }

    public JTextField getDiastolicThresholdTextField(){
        return this.diastolicThresholdTextField;
    }

    public JTable getPatientTable() {
        return patientTable;
    }
}
