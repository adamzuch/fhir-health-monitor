package View;

import javax.swing.*;
import java.awt.*;

/**
 * The Menu Pane which is shown after the practitioner logs in and provides them with the opportunity (as of now) to
 * view all their patients.
 */
class MenuPane extends JPanel {

    private JLabel practitionerNameLabel;
    private JButton viewPatientsButton;

    public MenuPane() {
        initComponents();
    }

    /**
     * Initialises the components for its view.
     */
    private void initComponents() {
        this.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JLabel welcomeLabel = new JLabel();
        practitionerNameLabel = new JLabel();
        viewPatientsButton = new JButton();


        welcomeLabel.setText("Welcome ");
        practitionerNameLabel.setText("");

        Font titleFont = welcomeLabel.getFont();
        welcomeLabel.setFont(new Font(titleFont.getName(), titleFont.getStyle(), 36));
        practitionerNameLabel.setFont(new Font (titleFont.getName(), titleFont.getStyle(), 36));

        viewPatientsButton.setText("View Patients");
        viewPatientsButton.setEnabled(false);

        titlePanel.add(welcomeLabel);
        titlePanel.add(practitionerNameLabel);
        buttonPanel.add(viewPatientsButton);

        this.add(titlePanel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);
    }

    public JLabel getPractitionerNameLabel() {
        return practitionerNameLabel;
    }

    public JButton getViewPatientsButton() {
        return viewPatientsButton;
    }
}
