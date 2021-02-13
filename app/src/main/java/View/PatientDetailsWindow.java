/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.MonitoredPatient;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Shows the extra detail of a patient and is called upon whenever the patient is clicked on the table.
 */
public class PatientDetailsWindow extends JFrame {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel addressLabel;
    private JTextField dobField;
    private JLabel dobLabel;
    private JTextArea addressField;
    private JTextField genderField;
    private JLabel genderLabel;
    private JPanel detailsPanel;
    private JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates new form PatientView
     */
    public PatientDetailsWindow(MonitoredPatient patient) {
        initComponents();

        //Set the pane title to the patient's name
        TitledBorder panelBorder = (TitledBorder) detailsPanel.getBorder();

        panelBorder.setTitle(patient.getFullName());

        //Set the field names of the patient
        dobField.setText(patient.getDateOfBirth());
        genderField.setText(patient.getPatientGender());


        addressField.setText(patient.getPatientAddress());


    }

    /**
     * Initialises the components for its view.
     */
    private void initComponents() {

        detailsPanel = new JPanel();
        dobLabel = new JLabel();
        genderField = new JTextField();
        addressLabel = new JLabel();
        genderLabel = new JLabel();
        dobField = new JTextField();
        scrollPane = new JScrollPane();
        addressField = new JTextArea();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        detailsPanel.setBorder(BorderFactory.createTitledBorder("Patient Name"));
        detailsPanel.setToolTipText("");

        dobLabel.setText("Date Of Birth:");

        genderField.setEditable(false);


        addressLabel.setText("Address:");

        genderLabel.setText("Gender:");

        dobField.setEditable(false);


        addressField.setEditable(false);
        addressField.setColumns(20);
        addressField.setRows(5);
        scrollPane.setViewportView(addressField);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(detailsPanel);
        detailsPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(genderLabel)
                    .addComponent(addressLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(genderField))
                .addGap(56, 56, 56))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(dobLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dobField, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dobLabel)
                    .addComponent(dobField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genderLabel)
                    .addComponent(genderField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addressLabel)
                    .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(detailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(detailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
}
