package pfe.model;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;

public class FolderInfoPanel extends JPanel {
    private int folderId;
    private Connection conn;
    private JTextArea notesArea;
    private JComboBox<String> statusCombo;
    private JDateChooser lastVisitDate;
    private SimpleDateFormat dateFormat;

    public FolderInfoPanel(int folderId) {
        this.folderId = folderId;
        this.conn = DatabaseConnection.getConnection();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadFolderInfo();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(StyleConstants.PANEL_BORDER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Status
        gbc.gridy = 0;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Statut:"), gbc);

        String[] statuses = {"actif", "archivé", "urgent"};
        statusCombo = new JComboBox<>(statuses);
        StyleConstants.styleComboBox(statusCombo);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        // Last visit date
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Dernière visite:"), gbc);

        lastVisitDate = new JDateChooser();
        lastVisitDate.setDateFormatString("dd/MM/yyyy");
        gbc.gridx = 1;
        formPanel.add(lastVisitDate, gbc);

        // Notes
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Notes:"), gbc);

        notesArea = new JTextArea(10, 40);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(notesArea);
        gbc.gridx = 1;
        formPanel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        StyleConstants.styleButton(saveButton);
        saveButton.addActionListener(e -> saveChanges());
        buttonPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFolderInfo() {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM medical_folders WHERE id = ?");
            stmt.setInt(1, folderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                statusCombo.setSelectedItem(rs.getString("status"));
                if (rs.getDate("last_visit_date") != null) {
                    lastVisitDate.setDate(rs.getDate("last_visit_date"));
                }
                notesArea.setText(rs.getString("notes"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des informations: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveChanges() {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE medical_folders SET status = ?, last_visit_date = ?, notes = ? WHERE id = ?");
            
            stmt.setString(1, (String) statusCombo.getSelectedItem());
            if (lastVisitDate.getDate() != null) {
                stmt.setDate(2, new java.sql.Date(lastVisitDate.getDate().getTime()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setString(3, notesArea.getText().trim());
            stmt.setInt(4, folderId);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                    "Modifications enregistrées avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement des modifications: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 