package pfe.model;

import javax.swing.*;
import java.awt.*;

public class Splash extends JFrame {
    public Splash() {
        initComponents();
    }

    private void initComponents() {
        // Initialize components
        JPanel jPanel1 = new JPanel();
        JLabel titleLabel = new JLabel("Hospital Appointment Management System");
        JLabel loadingLabel = new JLabel("wait...");
        JProgressBar jProgressBar1 = new JProgressBar();

        // Configure the splash screen
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jPanel1.setBackground(new Color(0, 104, 204)); 
        titleLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 255, 255)); // White text 
        loadingLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 16));
        loadingLabel.setForeground(new Color(200, 200, 200)); // Light gray 
        
        jProgressBar1.setIndeterminate(true); 
        jProgressBar1.setPreferredSize(new Dimension(350, 20)); 
        jProgressBar1.setForeground(new Color(0, 153, 255)); 
        jProgressBar1.setBorderPainted(false); 

        // Layout
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
        jPanel1.add(Box.createRigidArea(new Dimension(0, 30))); // Space
        jPanel1.add(titleLabel);
        jPanel1.add(Box.createRigidArea(new Dimension(0, 20))); 
        jPanel1.add(jProgressBar1);
        jPanel1.add(Box.createRigidArea(new Dimension(0, 20))); 
        jPanel1.add(loadingLabel);
        jPanel1.add(Box.createVerticalGlue());

        add(jPanel1);
        setSize(400, 200);
        setLocationRelativeTo(null); // Center the splash screen

        // Simulate loading
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(20); // Simulate loading time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dispose(); // Close splash
            new Login().setVisible(true); // Show login screen
        }).start();
    }
}
