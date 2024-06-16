import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PatientInsertionGUI extends JFrame {
    private JTextField patientNumberField;
    private JTextField fullNameField;
    private JTextField telephoneNumberField;
    private JTextField addressField;
    private JTextField maritalStatusField;
    private JTextField sexField;
    private JButton insertButton;
    private JButton removeButton;
    private JTable patientTable;
    private MainGUI mainGUI;

    public PatientInsertionGUI(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
        createView();

        setTitle("Patient Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Maximize the JFrame
        setPreferredSize(new Dimension(800, 600));  // Set the preferred size
        setMinimumSize(new Dimension(800, 600));  // Set the minimum size
        setLocationRelativeTo(null);  // Center the JFrame on screen
        setResizable(true);  // Allow resizing
        pack();  // Pack the components

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);  // Hide the PatientInsertionGUI
                mainGUI.showMainGUI();  // Show the main GUI
            }
        });        
    }

    private void createView() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        getContentPane().add(mainPanel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10));
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JLabel patientNumberLabel = new JLabel("Patient Number:");
        patientNumberField = new JTextField();
        patientNumberField.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(patientNumberLabel);
        inputPanel.add(patientNumberField);

        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameField = new JTextField();
        fullNameField.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(fullNameLabel);
        inputPanel.add(fullNameField);

        JLabel telephoneNumberLabel = new JLabel("Telephone Number:");
        telephoneNumberField = new JTextField();
        telephoneNumberField.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(telephoneNumberLabel);
        inputPanel.add(telephoneNumberField);

        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField();
        addressField.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(addressLabel);
        inputPanel.add(addressField);

        JLabel maritalStatusLabel = new JLabel("Marital Status:");
        maritalStatusField = new JTextField();
        maritalStatusField.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(maritalStatusLabel);
        inputPanel.add(maritalStatusField);

        JLabel sexLabel = new JLabel("Sex:");
        sexField = new JTextField();
        sexField.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(sexLabel);
        inputPanel.add(sexField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        insertButton = new JButton("Insert Patient");
        insertButton.setPreferredSize(new Dimension(150, 40));
        insertButton.addActionListener(new InsertPatientActionListener());
        buttonPanel.add(insertButton);

        removeButton = new JButton("Remove Patient");
        removeButton.setPreferredSize(new Dimension(150, 40));
        removeButton.addActionListener(new RemovePatientActionListener());
        buttonPanel.add(removeButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        patientTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(patientTable);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
    }

    private void displayPatientList() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "1234";

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Patient Number");
        model.addColumn("Full Name");
        model.addColumn("Telephone Number");
        model.addColumn("Address");
        model.addColumn("Marital Status");
        model.addColumn("Sex");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM patient";
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    String patientNumber = resultSet.getString("patient_number");
                    String fullName = resultSet.getString("full_name");
                    String telephoneNumber = resultSet.getString("telephone_number");
                    String address = resultSet.getString("address");
                    String maritalStatus = resultSet.getString("marital_status");
                    String sex = resultSet.getString("sex");

                    model.addRow(new Object[]{patientNumber, fullName, telephoneNumber, address, maritalStatus, sex});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching patient list: " + ex.getMessage());
        }

        patientTable.setModel(model);  // Set the model for the JTable
    }

    private class InsertPatientActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String patientNumber = patientNumberField.getText();
            String fullName = fullNameField.getText();
            String telephoneNumber = telephoneNumberField.getText();
            String address = addressField.getText();
            String maritalStatus = maritalStatusField.getText();
            String sex = sexField.getText();

            // Check for empty fields and show a warning message
            if (patientNumber.isEmpty() || fullName.isEmpty() || telephoneNumber.isEmpty() || address.isEmpty() || maritalStatus.isEmpty() || sex.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled out.");
                return;  // Exit the method without performing the insertion operation
            }

            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "postgres";
            String password = "1234";

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                String sql = "INSERT INTO patient (patient_number, full_name, telephone_number, address, marital_status, sex) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, patientNumber);
                    statement.setString(2, fullName);
                    statement.setString(3, telephoneNumber);
                    statement.setString(4, address);
                    statement.setString(5, maritalStatus);
                    statement.setString(6, sex);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Patient inserted successfully!");
                        clearFields();  // Optional: Clear input fields after insertion
                        displayPatientList();  // Refresh the patient list in the GUI
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to insert patient!");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error inserting patient: " + ex.getMessage());
            }
        }
    }

    private class RemovePatientActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String patientNumber = patientNumberField.getText();

            if (patientNumber.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a patient number.");
                return;  // Exit the method without performing the removal operation
            }

            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "postgres";
            String password = "1234";

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                String sql = "DELETE FROM patient WHERE patient_number = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, patientNumber);
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(null, "Patient removed successfully!");
                        clearFields();  // Optional: Clear input fields after removal
                        displayPatientList();  // Refresh the patient list in the GUI
                    } else {
                        JOptionPane.showMessageDialog(null, "No patient with the specified patient number.");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error removing patient: " + ex.getMessage());
            }
        }
    }

    // Helper method to clear input fields
    private void clearFields() {
        patientNumberField.setText("");
        fullNameField.setText("");
        telephoneNumberField.setText("");
        addressField.setText("");
        maritalStatusField.setText("");
        sexField.setText("");
    }
    
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI mainGUI = new MainGUI();  // Assuming you have a MainGUI class
            mainGUI.setVisible(true);
        });
    }
}