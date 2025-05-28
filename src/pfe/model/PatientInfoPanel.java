package pfe.model;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class PatientInfoPanel extends JPanel {
    private int patientId;
    private Connection conn;

    public PatientInfoPanel(int patientId) {
        this.patientId = patientId;
        this.conn = DatabaseConnection.getConnection();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loadPatientInfo();
    }

    private void loadPatientInfo() {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM patients WHERE id = ?");
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Create form panel
                JPanel formPanel = new JPanel(new GridBagLayout());
                formPanel.setBackground(Color.WHITE);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.WEST;

                // Add patient information fields
                addField(formPanel, "ID:", String.valueOf(rs.getInt("id")), gbc, 0);
                addField(formPanel, "Nom:", rs.getString("name"), gbc, 1);
                addField(formPanel, "Contact:", rs.getString("contact"), gbc, 2);
                addField(formPanel, "Email:", rs.getString("email"), gbc, 3);
                addField(formPanel, "Adresse:", rs.getString("address"), gbc, 4);

                // Add form to scroll pane
                JScrollPane scrollPane = new JScrollPane(formPanel);
                scrollPane.setBorder(null);
                add(scrollPane, BorderLayout.CENTER);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des informations du patient: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addField(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel valueComponent = new JLabel(value != null ? value : "");
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(valueComponent, gbc);
        
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
    }
} 