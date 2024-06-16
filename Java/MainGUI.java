import javax.swing.*;

public class MainGUI extends JFrame {
    private JButton patientButton;
    private JButton wardButton;

    public MainGUI() {
        setTitle("Hospital Management System");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on screen

        // Creating buttons
        patientButton = new JButton("Patient System");
        wardButton = new JButton("Ward System");

        // Adding action listeners
        patientButton.addActionListener(e -> showPatientSystem());
        wardButton.addActionListener(e -> showWardSystem());

        // Creating panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(patientButton);
        buttonPanel.add(wardButton);

        // Adding panel to the frame
        add(buttonPanel);

        setVisible(true);
    }

    private void showPatientSystem() {
        setVisible(false);  // Hide the main GUI
        SwingUtilities.invokeLater(() -> {
            PatientInsertionGUI patientGUI = new PatientInsertionGUI(this);  // Pass reference to MainGUI
            patientGUI.setVisible(true);
        });
    }

    private void showWardSystem() {
        setVisible(false);  // Hide the main GUI
        SwingUtilities.invokeLater(() -> {
            WardManagementGUI wardGUI = new WardManagementGUI(this);  // Pass reference to MainGUI
            wardGUI.setVisible(true);
        });
    }

    public void showMainGUI() {
        setVisible(true);  // Show the main GUI
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
