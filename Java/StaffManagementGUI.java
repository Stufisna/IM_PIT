import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;

public class StaffManagementGUI extends JFrame {

    private JTextField staffNumberField;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneNumberField;
    private JTextField dobField;
    private JTextField sexField;
    private JTextField ninField;
    private JTextField positionField;
    private JTextField salaryField;
    private JTextField salaryScaleField;
    private JTextField qualificationField;
    private JTextField experienceField;
    private JTextField wardNumberField;
    private JTextField allocatedField;

    private JButton insertButton;
    private JButton deleteButton;
    private JButton backButton;

    private Connection connection;
    private MainGUI mainGUI;

    public StaffManagementGUI(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
        initializeUI();
        connectToDatabase();
    }

    private void initializeUI() {
        setTitle("Staff Management");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(17, 2));

        add(new JLabel("Staff Number:"));
        staffNumberField = new JTextField();
        add(staffNumberField);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Full Address:"));
        addressField = new JTextField();
        add(addressField);

        add(new JLabel("Phone Number:"));
        phoneNumberField = new JTextField();
        add(phoneNumberField);

        add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        dobField = new JTextField();
        add(dobField);

        add(new JLabel("Sex:"));
        sexField = new JTextField();
        add(sexField);

        add(new JLabel("NIN:"));
        ninField = new JTextField();
        add(ninField);

        add(new JLabel("Position Held:"));
        positionField = new JTextField();
        add(positionField);

        add(new JLabel("Current Salary:"));
        salaryField = new JTextField();
        add(salaryField);

        add(new JLabel("Salary Scale:"));
        salaryScaleField = new JTextField();
        add(salaryScaleField);

        add(new JLabel("Qualification:"));
        qualificationField = new JTextField();
        add(qualificationField);

        add(new JLabel("Work Experience Details:"));
        experienceField = new JTextField();
        add(experienceField);

        add(new JLabel("Ward Number:"));
        wardNumberField = new JTextField();
        add(wardNumberField);

        add(new JLabel("Allocated:"));
        allocatedField = new JTextField();
        add(allocatedField);

        insertButton = new JButton("Insert Staff");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InsertStaffWorker().execute();
            }
        });
        add(insertButton);

        deleteButton = new JButton("Delete Staff");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DeleteStaffWorker().execute();
            }
        });
        add(deleteButton);

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainGUI.setVisible(true);
                dispose();
            }
        });
        add(backButton);

        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "postgres";
            String password = "1234";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class InsertStaffWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            if (staffNumberField.getText().isEmpty() || nameField.getText().isEmpty() ||
                addressField.getText().isEmpty() || phoneNumberField.getText().isEmpty() ||
                dobField.getText().isEmpty() || sexField.getText().isEmpty() ||
                ninField.getText().isEmpty() || positionField.getText().isEmpty() ||
                salaryField.getText().isEmpty() || salaryScaleField.getText().isEmpty() ||
                qualificationField.getText().isEmpty() || experienceField.getText().isEmpty() ||
                wardNumberField.getText().isEmpty() || allocatedField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(StaffManagementGUI.this, "Please fill in all fields.");
                return null;
            }
    
            try {
                String query = "CALL insert_staff(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, staffNumberField.getText());
                    stmt.setString(2, nameField.getText());
                    stmt.setString(3, addressField.getText());
                    stmt.setString(4, phoneNumberField.getText());
                    stmt.setDate(5, Date.valueOf(dobField.getText()));
                    stmt.setString(6, sexField.getText());
                    stmt.setString(7, ninField.getText());
                    stmt.setString(8, positionField.getText());
                    stmt.setBigDecimal(9, new BigDecimal(salaryField.getText()));
                    stmt.setString(10, salaryScaleField.getText());
                    stmt.setString(11, qualificationField.getText());
                    stmt.setString(12, experienceField.getText());
                    stmt.setInt(13, Integer.parseInt(wardNumberField.getText()));
                    stmt.setInt(14, Integer.parseInt(allocatedField.getText()));
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(StaffManagementGUI.this, "Error inserting staff: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(StaffManagementGUI.this, "Invalid number format for Ward Number or Allocated.");
            }
    
            return null;
        }
    
        @Override
        protected void done() {
            try {
                get();
                JOptionPane.showMessageDialog(StaffManagementGUI.this, "Staff inserted successfully!");
                clearFields();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DeleteStaffWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            String query = "CALL delete_staff(?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, staffNumberField.getText());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(StaffManagementGUI.this, "Error deleting staff: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                JOptionPane.showMessageDialog(StaffManagementGUI.this, "Staff deleted successfully!");
                clearFields();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        staffNumberField.setText("");
        nameField.setText("");
        addressField.setText("");
        phoneNumberField.setText("");
        dobField.setText("");
        sexField.setText("");
        ninField.setText("");
        positionField.setText("");
        salaryField.setText("");
        salaryScaleField.setText("");
        qualificationField.setText("");
        experienceField.setText("");
        wardNumberField.setText("");
        allocatedField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI mainGUI = new MainGUI();
            new StaffManagementGUI(mainGUI);
        });
    }
}
