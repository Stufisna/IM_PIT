import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class WardManagementGUI extends JFrame {
    private MainGUI mainGUI;
    private JTextField wardNumberField, locationField, totalBedsField, telephoneExtensionField;
    private JButton insertButton, removeButton, backButton;
    private JTable wardTable;
    private DefaultTableModel tableModel;

    public WardManagementGUI(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
        setTitle("Ward Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Creating GUI components
        wardNumberField = new JTextField(15);
        locationField = new JTextField(15);
        totalBedsField = new JTextField(15);
        telephoneExtensionField = new JTextField(15);

        insertButton = new JButton("Insert Ward");
        removeButton = new JButton("Remove Ward");
        backButton = new JButton("Back");

        // Adding action listeners
        insertButton.addActionListener(new InsertWardListener());
        removeButton.addActionListener(new RemoveWardListener());
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close only this window
                mainGUI.setVisible(true); // Show the main GUI
            }
        });

        // Setting up the layout
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2)); // Changed to 6 rows for the back button
        inputPanel.add(new JLabel("Ward Number:"));
        inputPanel.add(wardNumberField);
        inputPanel.add(new JLabel("Location:"));
        inputPanel.add(locationField);
        inputPanel.add(new JLabel("Total Number of Beds:"));
        inputPanel.add(totalBedsField);
        inputPanel.add(new JLabel("Telephone Extension:"));
        inputPanel.add(telephoneExtensionField);
        inputPanel.add(insertButton);
        inputPanel.add(removeButton);
        inputPanel.add(backButton); // Add the back button to the input panel

        // Setting up the table model
        String[] columnNames = {"Ward Number", "Location", "Total Beds", "Telephone Extension"};
        tableModel = new DefaultTableModel(columnNames, 0);
        wardTable = new JTable(tableModel);

        // Adding components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(wardTable), BorderLayout.CENTER);

        // Initial refresh to display current wards
        refreshWardList();

        // Window listener to return to main GUI on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dispose(); // Close only this window
                mainGUI.setVisible(true); // Show the main GUI
            }
        });
    }

    private void refreshWardList() {
        // Clear the table before refreshing
        tableModel.setRowCount(0);
        List<String[]> wards = fetchWardsFromDatabase();
        for (String[] ward : wards) {
            tableModel.addRow(ward);
        }
    }

    private List<String[]> fetchWardsFromDatabase() {
        List<String[]> wards = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ward")) {

            while (rs.next()) {
                String wardNumber = String.valueOf(rs.getInt("ward_number"));
                String location = rs.getString("location");
                String totalBeds = String.valueOf(rs.getInt("total_number_of_beds"));
                String telephoneExtension = String.valueOf(rs.getInt("telephone_extension_number"));
                wards.add(new String[]{wardNumber, location, totalBeds, telephoneExtension});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return wards;
    }

    private void clearFields() {
        wardNumberField.setText("");
        locationField.setText("");
        totalBedsField.setText("");
        telephoneExtensionField.setText("");
    }

    private class InsertWardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int response = JOptionPane.showConfirmDialog(
                    WardManagementGUI.this,
                    "Are you sure you want to insert this ward?",
                    "Confirm Insert",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (response == JOptionPane.YES_OPTION) {
                String wardNumber = wardNumberField.getText();
                String location = locationField.getText();
                String totalBeds = totalBedsField.getText();
                String telephoneExtension = telephoneExtensionField.getText();

                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234");
                     CallableStatement stmt = conn.prepareCall("CALL insert_ward(?, ?, ?, ?)")) {

                    stmt.setInt(1, Integer.parseInt(wardNumber));
                    stmt.setString(2, location);
                    stmt.setInt(3, Integer.parseInt(totalBeds));
                    stmt.setInt(4, Integer.parseInt(telephoneExtension));
                    stmt.execute();

                    refreshWardList(); // Refresh the list after insertion
                    clearFields(); // Clear the input fields
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(WardManagementGUI.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class RemoveWardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int response = JOptionPane.showConfirmDialog(
                    WardManagementGUI.this,
                    "Are you sure you want to remove this ward?",
                    "Confirm Remove",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (response == JOptionPane.YES_OPTION) {
                String wardNumber = wardNumberField.getText();

                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "1234");
                     CallableStatement stmt = conn.prepareCall("CALL remove_ward(?)")) {

                    stmt.setInt(1, Integer.parseInt(wardNumber));
                    stmt.execute();

                    refreshWardList(); // Refresh the list after removal
                    clearFields(); // Clear the input fields
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(WardManagementGUI.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI mainGUI = new MainGUI(); // Instantiate MainGUI here if needed
            WardManagementGUI frame = new WardManagementGUI(mainGUI);
            frame.setVisible(true);
        });
    }
}
