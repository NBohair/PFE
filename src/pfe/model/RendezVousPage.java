package pfe.model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import pfe.DB.RendezVousDAO; // Ensure correct import path
import pfe.service.RendezVous; // Ensure correct import path
import pfe.service.User; // Ensure correct import path

public class RendezVousPage extends JFrame {
    // Declare UI components
    private JComboBox<String> doctorCombo; // For selecting doctors
    private DefaultTableModel tableModel; // For the appointments table
    private JFormattedTextField dateField; // Field for date input
    private JFormattedTextField timeField; // Field for time input
    private JTextArea reasonArea; // Field for reason input
    private User user; // Currently logged-in user

    // Constructor
    public RendezVousPage(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User  cannot be null");
        }
        this.user = user;
        initComponents(); // Initialize UI components
        populateDoctorList(); // Populate the doctor list
        loadRendezVous(); // Load existing appointments
    }

    // Method to initialize UI components
    private void initComponents() {
        setTitle("Manage Rendez-Vous");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        // Initialize components
        doctorCombo = new JComboBox<>();
        tableModel = new DefaultTableModel(new String[]{"Date", "Time", "Doctor", "Reason"}, 0);
        JTable appointmentTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(appointmentTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Your Rendez-Vous"));

        // Form panel for booking new rendez-vous
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book New Rendez-Vous"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        gbc.anchor = GridBagConstraints.WEST;

        // Date field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        dateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        dateField.setColumns(10);
        gbc.gridx = 1; formPanel.add(dateField, gbc);

        // Time field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Time (HH:MM):"), gbc);
        timeField = new JFormattedTextField(new SimpleDateFormat("HH:mm"));
        timeField.setColumns(5);
        gbc.gridx = 1; formPanel.add(timeField, gbc);

        // Doctor selection
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1; formPanel.add(doctorCombo, gbc);

        // Reason
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("Reason:"), gbc);
        reasonArea = new JTextArea(3, 20);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        gbc.gridy = 4; formPanel.add(reasonScroll, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookBtn = new JButton("Book");
        bookBtn.addActionListener(e -> bookRendezVous());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(bookBtn);
        btnPanel.add(cancelBtn);
        gbc.gridy = 5; formPanel.add(btnPanel, gbc);

        // Main panel layout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.EAST);

        // Add main panel to frame
        add(mainPanel);
        setVisible(true);
    }

    // Method to populate the doctor list
    private void populateDoctorList() {
        List<String> doctors = new RendezVousDAO().getAllDoctorNames();
        for (String d : doctors) {
            doctorCombo.addItem(d);
        }
    }

    // Method to load existing rendez-vous
    private void loadRendezVous() {
        tableModel.setRowCount(0);
        List<RendezVous> list = new RendezVousDAO().getRendezVousByUser (user.getUserId());
        for (RendezVous r : list) {
            tableModel.addRow(new Object[]{r.getDateTime(), r.getDoctorName(), r.getReason()});
        }
    }

    // Method to book a new rendez-vous
    private void bookRendezVous() {
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String doctor = (String) doctorCombo.getSelectedItem();
        String reason = reasonArea.getText().trim();

        if (date.isEmpty() || time.isEmpty() || doctor == null || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = new RendezVousDAO().scheduleRendezVous(user.getUserId(), doctor, date + " " + time);
        if (success) {
            JOptionPane.showMessageDialog(this, "Rendez-Vous booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRendezVous(); // Reload the appointments
        } else {
            JOptionPane.showMessageDialog(this, "Failed to book Rendez-Vous.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User dummyUser  = new User(); // Replace 
            dummyUser .setUserId("1"); // Set a test user ID for demonstration
            new RendezVousPage(dummyUser ).setVisible(true);
        });
    }
}
