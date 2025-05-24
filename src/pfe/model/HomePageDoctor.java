package pfe.model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePageDoctor extends JFrame {
    private JPanel mainPanel;
    private JTextArea appointmentArea; // To display appointments
    private JTextField nameField; // To display doctor name
    private JTextField specialtyField; // To display doctor's specialty

    public HomePageDoctor() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Doctor Home Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the main window

        // Create main panel with a card layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Create header
        JLabel headerLabel = new JLabel("Welcome to the Doctor Home Page", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space below header

        // Create doctor information card
        JPanel doctorInfoCard = createCardPanel("Doctor Information");
        doctorInfoCard.setLayout(new GridLayout(2, 2));
        doctorInfoCard.add(new JLabel("Name:"));
        nameField = new JTextField();
        nameField.setEditable(false); // Make it read-only
        doctorInfoCard.add(nameField);
        doctorInfoCard.add(new JLabel("Specialty:"));
        specialtyField = new JTextField();
        specialtyField.setEditable(false); // Make it read-only
        doctorInfoCard.add(specialtyField);
        mainPanel.add(doctorInfoCard);

        // Create appointment area card
        JPanel appointmentCard = createCardPanel("Appointments");
        appointmentArea = new JTextArea();
        appointmentArea.setEditable(false);
        appointmentArea.setLineWrap(true);
        appointmentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(appointmentArea);
        appointmentCard.setLayout(new BorderLayout());
        appointmentCard.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(appointmentCard);

        // Create buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton viewAppointmentsButton = createButton("View Appointments", e -> viewAppointments());
        JButton managePatientsButton = createButton("Manage Patients", e -> managePatients());
        JButton logoutButton = createButton("Logout", e -> logout());

        buttonPanel.add(viewAppointmentsButton);
        buttonPanel.add(managePatientsButton);
        buttonPanel.add(logoutButton);
        mainPanel.add(buttonPanel);

        // Add main panel to frame
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createCardPanel(String title) {
        JPanel cardPanel = new JPanel();
        cardPanel.setBorder(BorderFactory.createTitledBorder(title));
        cardPanel.setBackground(Color.WHITE);
        return cardPanel;
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 153, 0)); // Button color
        button.setForeground(Color.WHITE); // Text color
        button.setFocusPainted(false); // Remove focus border
        button.setPreferredSize(new Dimension(150, 40)); // Button size
        button.addActionListener(action);
        return button;
    }

    private void viewAppointments() {
        // Logic to fetch and display doctor's appointments from the database
        appointmentArea.setText("Fetching appointments..."); // Placeholder text
        // Example: Fetch from database and display
        appointmentArea.setText("1. Appointment with Patient A on 2023-10-01\n2. Appointment with Patient B on 2023-10-05");
    }

    private void managePatients() {
        // Logic to manage patients (e.g., view patient list, edit patient info)
        JOptionPane.showMessageDialog(this, "Manage Patients functionality is not yet implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        // Logic to log out the doctor and return to the login screen
        dispose(); // Close the current window
        new Login().setVisible(true); // Show the login screen again
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomePageDoctor().setVisible(true);
        });
    }
}
