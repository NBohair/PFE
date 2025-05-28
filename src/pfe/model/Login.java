package pfe.model;

import javax.swing.*;
import pfe.DB.DBConnection; // Keep for potential future use, but login logic moves
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// Remove direct SQL imports as they are handled by Authentication class
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
import pfe.service.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private Authentication auth; // Add Authentication instance

    public Login() {
        auth = new Authentication(); // Initialize Authentication
        initComponents();
    }

    private void initComponents() {
        // Set up the main frame
        setTitle("Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 300);
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
        JLabel titleLabel = new JLabel("Gestion des rendez-vous des hôpitaux");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204)); // Title color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0); // Add space below the title
        mainPanel.add(titleLabel, gbc);

        // Username label and field
        JLabel usernameLabel = new JLabel("Username (Email or Phone):");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0); // Add space below the label
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1)); // Border color
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1)); // Border color
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Button panel for login and cancel buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240)); // Match main panel color
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the right

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 102, 204)); // Button color
        loginButton.setForeground(Color.WHITE); // Text color
        loginButton.setFocusPainted(false); // Remove focus border
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin(); // Call the updated handleLogin method
            }
        });
        buttonPanel.add(loginButton);

        // Sign Up button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setBackground(new Color(0, 153, 0)); // Button color
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ensure SignUp class exists and is accessible
                 new SignUp().setVisible(true);
                 // Consider disposing the login window or hiding it
                 // dispose(); 
            }
        });
        buttonPanel.add(signUpButton);

        // Cancel button
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(204, 0, 0)); // Button color
        cancelButton.setForeground(Color.WHITE); // Text color
        cancelButton.setFocusPainted(false); // Remove focus border
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Close application
            }
        });
        buttonPanel.add(cancelButton);

        // Add components to the main panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);

        // Add main panel to frame
        add(mainPanel);
    }

    // Updated handleLogin method using Authentication class
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User authenticatedUser = auth.login(username, password);

        if (authenticatedUser != null) {
            // User authenticated successfully
            JOptionPane.showMessageDialog(this, "Login successful! Welcome " + authenticatedUser.getUsername(), "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Open the appropriate home page based on user role
            // Assuming role is stored in authenticatedUser.getRole()
            // This part needs the actual HomePage classes (HomePageAdmin, HomePageDoctor, HomePageUser)
            // For now, let's default to HomePageAdmin as in the original code, but ideally check the role.
            String role = authenticatedUser.getRole();
            if ("admin".equalsIgnoreCase(role)) {
                 new HomePageAdmin().setVisible(true);
            } else if ("doctor".equalsIgnoreCase(role)) {
                 new HomePageDoctor().setVisible(true);
            } else { // Default to user or handle other roles
                 new HomePageUser(authenticatedUser).setVisible(true);
            }

            dispose(); // Close the login window
        } else {
            // Authentication failed (message handled within auth.login or show a generic one here)
            // JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            // The Authentication class already shows a message, so maybe no need for another one here.
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Erreur lors de l'initialisation du Look and Feel: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Impossible de charger le Look and Feel système: " + ex.getMessage());
            }
        }
        
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}

