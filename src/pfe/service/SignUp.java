package pfe.service;

import javax.swing.*;

import pfe.model.Login;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUp extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usernameField; // Username field
    private JTextField emailField; // Email field
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private Authentication auth; // Instance of Authentication class

    public SignUp() {
        auth = new Authentication(); // Initialize the Authentication instance
        initComponents();
    }

    private void initComponents() {
        // Set up the main frame
        setTitle("Sign Up");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 500); // Increased size for better layout
        setLocationRelativeTo(null); // Center the window
        setResizable(false); // Prevent resizing

        // Create main panel with a background color
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel titleLabel = new JLabel("Create an Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204)); // Title color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0); // Add space below the title
        mainPanel.add(titleLabel, gbc);

        // First Name label and field
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0); // Add space below the label
        mainPanel.add(firstNameLabel, gbc);

        firstNameField = new JTextField(20);
        firstNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        firstNameField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1)); // Border color
        gbc.gridx = 1;
        mainPanel.add(firstNameField, gbc);

        // Last Name label and field
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(lastNameLabel, gbc);

        lastNameField = new JTextField(20);
        lastNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        lastNameField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1)); // Border color
        gbc.gridx = 1;
        mainPanel.add(lastNameField, gbc);

        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20); // Username field
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1)); // Border color
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField(20); // Email field
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1)); // Border color
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1)); // Border color
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Confirm Password label and field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1)); // Border color
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // Button panel for sign up and cancel buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240)); // Match main panel color
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the right

        // Sign Up button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setBackground(new Color(0, 153, 0)); // Button color
        signUpButton.setForeground(Color.WHITE); // Text color
        signUpButton.setFocusPainted(false); // Remove focus border
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignUp(); // Call the handleSignUp method
            }
        });
        buttonPanel.add(signUpButton);

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(204, 0, 0)); // Button color
        cancelButton.setForeground(Color.WHITE); // Text color
        cancelButton.setFocusPainted(false); // Remove focus border
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the sign-up window
            }
        });
        buttonPanel.add(cancelButton);

        // Add components to the main panel
        gbc.gridx = 0;
        gbc.gridy = 7; // Adjusted to the next row
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0); // Add space above the button panel
        mainPanel.add(buttonPanel, gbc);

        // Add main panel to frame
        add(mainPanel);
    }

    private void handleSignUp() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText(); // Capture username
        String email = emailField.getText(); // Capture email
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Input validation
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser  = new User();
        newUser .setUsername(username);
        newUser .setEmail(email); // Set email
        newUser .setPassword(password); // Consider hashing the password
        newUser .setFirstName(firstName); // Set first name
        newUser .setLastName(lastName); // Set last name
        newUser .setRole("user"); // Automatically set role to user

        boolean success = auth.register(newUser ); // Use the Authentication class to register

        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new Login().setVisible(true); // Redirect to login screen
            dispose(); // Close the sign-up window
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp().setVisible(true));
    }
}
