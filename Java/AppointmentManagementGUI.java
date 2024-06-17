import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AppointmentManagementGUI extends JFrame {
    private JTextField appointmentNumField, examRoomField, patientNumField, staffNumField;
    private JTextField dateTimeField;
    private JButton insertButton, removeButton;
    private JTextArea displayArea;

    private Connection connection;

    public AppointmentManagementGUI() {
        initComponents();
        initDatabaseConnection();
    }

    private void initComponents() {
        setTitle("Appointment Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Appointment Number:"));
        appointmentNumField = new JTextField();
        inputPanel.add(appointmentNumField);

        inputPanel.add(new JLabel("Examination Room:"));
        examRoomField = new JTextField();
        inputPanel.add(examRoomField);

        inputPanel.add(new JLabel("Date and Time (YYYY-MM-DD HH:MM:SS):"));
        dateTimeField = new JTextField();
        inputPanel.add(dateTimeField);

        inputPanel.add(new JLabel("Patient Number:"));
        patientNumField = new JTextField();
        inputPanel.add(patientNumField);

        inputPanel.add(new JLabel("Staff Number:"));
        staffNumField = new JTextField();
        inputPanel.add(staffNumField);

        insertButton = new JButton("Insert Appointment");
        removeButton = new JButton("Remove Appointment");
        inputPanel.add(insertButton);
        inputPanel.add(removeButton);

        add(inputPanel, BorderLayout.NORTH);

        displayArea = new JTextArea(10, 30);
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertAppointment();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAppointment();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void initDatabaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234");
            displayArea.append("Database connected successfully.\n");
        } catch (SQLException e) {
            displayArea.append("Database connection failed: " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void insertAppointment() {
        String appointmentNum = appointmentNumField.getText();
        String examRoom = examRoomField.getText();
        String dateTime = dateTimeField.getText();
        String patientNum = patientNumField.getText();
        String staffNum = staffNumField.getText();

        String sql = "CALL insert_appointment(?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(appointmentNum));
            stmt.setString(2, examRoom);
            stmt.setTimestamp(3, Timestamp.valueOf(dateTime));
            stmt.setString(4, patientNum);
            stmt.setString(5, staffNum);
            stmt.executeUpdate();
            displayArea.append("Appointment inserted successfully.\n");
        } catch (SQLException e) {
            displayArea.append("Error inserting appointment: " + e.getMessage() + "\n");
        } catch (NumberFormatException e) {
            displayArea.append("Invalid appointment number format.\n");
        } catch (IllegalArgumentException e) {
            displayArea.append("Invalid date and time format.\n");
        }
    }

    private void removeAppointment() {
        String appointmentNum = appointmentNumField.getText();

        String sql = "CALL remove_appointment(?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(appointmentNum));
            stmt.executeUpdate();
            displayArea.append("Appointment removed successfully.\n");
        } catch (SQLException e) {
            displayArea.append("Error removing appointment: " + e.getMessage() + "\n");
        } catch (NumberFormatException e) {
            displayArea.append("Invalid appointment number format.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AppointmentManagementGUI().setVisible(true);
            }
        });
    }
}
