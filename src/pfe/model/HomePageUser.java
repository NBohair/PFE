package pfe.model;

import javax.swing.*;
import pfe.service.User;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePageUser  extends JFrame {
    private JPanel mainPanel;
    private JTextArea appointmentArea;
    private JTextField nameField;
    private JTextField contactField;

    public HomePageUser (User user) {
        if (user == null) {
            throw new IllegalArgumentException("User  cannot be null");
        }
        initComponents(user);
    }

    private void initComponents(User user) {
        setTitle("User  Home Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Welcome to the User Home Page", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // User Information Card
        JPanel userInfoCard = createCardPanel("User  Information");
        userInfoCard.setLayout(new GridLayout(2, 2));
        userInfoCard.add(new JLabel("Name:"));
        nameField = new JTextField(user.getUsername());
        nameField.setEditable(false);
        userInfoCard.add(nameField);
        userInfoCard.add(new JLabel("Contact:"));
        contactField = new JTextField();
        userInfoCard.add(contactField);
        mainPanel.add(userInfoCard);

        // Appointments Card
        JPanel appointmentCard = createCardPanel("Appointments");
        appointmentArea = new JTextArea();
        appointmentArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(appointmentArea);
        appointmentCard.setLayout(new BorderLayout());
        appointmentCard.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(appointmentCard);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JButton viewAppointmentsButton = createButton("View Appointments", e -> viewAppointments());
        JButton addRendezVousButton = createButton("Add Rendezvous", e -> openRendezVousPage(user)); // Open rendezvous page

        buttonPanel.add(viewAppointmentsButton);
        buttonPanel.add(addRendezVousButton); // Add the rendezvous button next to the view appointments button

        JButton editProfileButton = createButton("Edit Profile", e -> editProfile());
        JButton logoutButton = createButton("Logout", e -> logout());

        buttonPanel.add(editProfileButton);
        buttonPanel.add(logoutButton);
        mainPanel.add(buttonPanel);

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
        button.setBackground(new Color(0, 153, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(action);
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private void openRendezVousPage(User user) {
        new RendezVousPage(user); // Pass the user object to the existing RendezVousPage
    }

    private void viewAppointments() {
        appointmentArea.setText("Fetching appointments...");
        // Logic to fetch and display user appointments from the database
    }

    private void editProfile() {
        String newContact = JOptionPane.showInputDialog(this, "Enter new contact:", contactField.getText());
        if (newContact != null && !newContact.trim().isEmpty()) {
            contactField.setText(newContact);
            // Update the database with the new contact
        }
    }

    private void logout() {
        dispose();
        new Login().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User user = new User(); // Replace with actual user retrieval logic
            user.setUsername("Test User"); // Set a test username for demonstration
            new HomePageUser (user).setVisible(true);
        });
    }
}
