package View;

import javax.swing.*;

/**
 * The Login View of the system.
 */
class LoginPane extends JPanel {
    private GroupLayout layout;
    private JButton enterButton;
    private JTextField idInput;
    private JLabel idPrompt;

    LoginPane() {
        layout = new GroupLayout(this);
        this.setLayout(layout);

        initComponents();
    }

    /**
     * Initialises the components for its view.
     */
    private void initComponents() {
        enterButton = new JButton();
        idInput = new JTextField();
        idPrompt = new JLabel();

        enterButton.setText("Enter");

        idPrompt.setText("Enter Practitioner ID:");
        idInput.setColumns(10);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(50, 250, 250)
                        .addComponent(idPrompt)
                        .addGroup(layout.createParallelGroup()
                                .addGap(50, 250, 250)
                                .addComponent(idInput)
                                .addGap(50, 250, Short.MAX_VALUE))
                        .addComponent(enterButton)
                        .addGap(50, 250, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(50, 250, 250)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addGap(50, 250, 250)
                                .addComponent(idPrompt)
                                .addComponent(idInput)
                                .addComponent(enterButton)
                                .addGap(50, 250, Short.MAX_VALUE))
                        .addGap(50, 250, Short.MAX_VALUE)
        );
    }

    JButton getEnterButton() {
        return enterButton;
    }

    public JTextField getIdInput() {
        return idInput;
    }
}
