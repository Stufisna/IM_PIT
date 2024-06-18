import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HospitalAppointmentGUI extends JFrame {
    private JTextField appointmentNumberField;
    private JTextField examinationRoomField;
    private JTextField dateTimeField;
    private JTextField patientNumberField;
    private JTextField staffNumberField;
    private DefaultListModel<String> appointmentListModel;
    private JList<String> appointmentList;
    private JButton backButton;
    private MainGUI mainGUI;

    public HospitalAppointmentGUI(MainGUI mainGUI) {
        this.mainGUI = mainGUI;

        // Initialize JFrame properties
        setTitle("Hospital Appointment Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Initialize components
        appointmentNumberField = new JTextField(10);
        examinationRoomField = new JTextField(10);
        dateTimeField = new JTextField(10);
        patientNumberField = new JTextField(10);
        staffNumberField = new JTextField(10);
        JButton addButton = new JButton("Add Appointment");
        JButton refreshButton = new JButton("Refresh List");
        backButton = new JButton("Back");

        // Layout for input fields
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.add(new JLabel("Appointment Number:"));
        inputPanel.add(appointmentNumberField);
        inputPanel.add(new JLabel("Examination Room:"));
        inputPanel.add(examinationRoomField);
        inputPanel.add(new JLabel("Date and Time:"));
        inputPanel.add(dateTimeField);
        inputPanel.add(new JLabel("Patient Number:"));
        inputPanel.add(patientNumberField);
        inputPanel.add(new JLabel("Staff Number:"));
        inputPanel.add(staffNumberField);
        inputPanel.add(addButton);
        inputPanel.add(refreshButton);

        // Panel for the back button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.add(backButton);

        // Panel for the appointment list
        appointmentListModel = new DefaultListModel<>();
        appointmentList = new JList<>(appointmentListModel);
        JScrollPane listScrollPane = new JScrollPane(appointmentList);

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(listScrollPane, BorderLayout.CENTER);
        add(backPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAppointment();
                refreshAppointmentList();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAppointmentList();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainGUI.showMainGUI();
                setVisible(false);
            }
        });

        // Refresh the appointment list on startup
        refreshAppointmentList();
    }

    // Method to add appointment to the database
    private void addAppointment() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "1234";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO appointment (appointment_number, examination_room, date_and_time_of_appointment, patient_number, staff_number) VALUES (?, ?, CAST(? AS TIMESTAMP), ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(appointmentNumberField.getText()));
            statement.setString(2, examinationRoomField.getText());
            statement.setString(3, dateTimeField.getText()); // Assuming dateTimeField.getText() returns a valid timestamp string
            statement.setString(4, patientNumberField.getText());
            statement.setString(5, staffNumberField.getText());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Appointment added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add appointment.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Method to refresh the appointment list from the database
    private void refreshAppointmentList() {
        appointmentListModel.clear();

        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "1234";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT * FROM appointment";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int appointmentNumber = resultSet.getInt("appointment_number");
                String examinationRoom = resultSet.getString("examination_room");
                String dateTime = resultSet.getString("date_and_time_of_appointment");
                String patientNumber = resultSet.getString("patient_number");
                String staffNumber = resultSet.getString("staff_number");

                String appointmentInfo = "Appointment Number: " + appointmentNumber +
                        ", Examination Room: " + examinationRoom +
                        ", Date and Time: " + dateTime +
                        ", Patient Number: " + patientNumber +
                        ", Staff Number: " + staffNumber;

                appointmentListModel.addElement(appointmentInfo);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HospitalAppointmentGUI gui = new HospitalAppointmentGUI(null);
            gui.setVisible(true);
            gui.refreshAppointmentList(); // Refresh list on startup
        });
    }
}
