package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ManageDoctors extends JPanel {
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private Connection conn;
    private JTextField searchField;
    private JComboBox<String> specialtyFilter;

    public ManageDoctors() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        conn = DatabaseConnection.getConnection();
        initComponents();
        loadDoctors();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Gestion des Médecins");
        add(headerPanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(StyleConstants.PANEL_BORDER);

        searchField = new JTextField(20);
        StyleConstants.styleTextField(searchField);
        searchField.setPreferredSize(new Dimension(200, 30));

        specialtyFilter = new JComboBox<>();
        loadSpecialties();
        StyleConstants.styleComboBox(specialtyFilter);

        JButton searchButton = new JButton("Rechercher");
        StyleConstants.styleButton(searchButton);
        searchButton.addActionListener(e -> loadDoctors());

        searchPanel.add(new JLabel("Rechercher:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Spécialité:"));
        searchPanel.add(specialtyFilter);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Nom", "Spécialité", "Contact", "Actions", "Supprimer"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setRowHeight(35);
        doctorTable.setShowGrid(true);
        doctorTable.setGridColor(new Color(230, 230, 230));
        doctorTable.getTableHeader().setReorderingAllowed(false);
        doctorTable.getTableHeader().setBackground(new Color(240, 240, 240));
        doctorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < doctorTable.getColumnCount() - 2; i++) {
            doctorTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Action columns
        doctorTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("Modifier"));
        doctorTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), "Modifier"));
        doctorTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("Supprimer"));
        doctorTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), "Supprimer"));

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton addButton = new JButton("Ajouter un médecin");
        StyleConstants.styleButton(addButton);
        addButton.addActionListener(e -> showDoctorDialog(null));

        buttonsPanel.add(addButton);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadSpecialties() {
        specialtyFilter.addItem("Toutes les spécialités");
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT DISTINCT specialty FROM doctors ORDER BY specialty");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                specialtyFilter.addItem(rs.getString("specialty"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDoctors() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT * FROM doctors WHERE 1=1";
            String search = searchField.getText().trim();
            String specialty = (String) specialtyFilter.getSelectedItem();

            if (!search.isEmpty()) {
                query += " AND (name LIKE ? OR contact LIKE ?)";
            }
            if (specialty != null && !specialty.equals("Toutes les spécialités")) {
                query += " AND specialty = ?";
            }
            query += " ORDER BY name";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int paramIndex = 1;

                if (!search.isEmpty()) {
                    String searchPattern = "%" + search + "%";
                    stmt.setString(paramIndex++, searchPattern);
                    stmt.setString(paramIndex++, searchPattern);
                }
                if (specialty != null && !specialty.equals("Toutes les spécialités")) {
                    stmt.setString(paramIndex, specialty);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("specialty"),
                            rs.getString("contact"),
                            "Modifier",
                            "Supprimer"
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Erreur lors du chargement des médecins: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                errorMessage,
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDoctorDialog(Integer doctorId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            doctorId == null ? "Ajouter un médecin" : "Modifier le médecin", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name field
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Nom:"), gbc);

        JTextField nameField = new JTextField(30);
        StyleConstants.styleTextField(nameField);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Specialty field
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Spécialité:"), gbc);

        JTextField specialtyField = new JTextField(30);
        StyleConstants.styleTextField(specialtyField);
        gbc.gridx = 1;
        panel.add(specialtyField, gbc);

        // Contact field
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Contact:"), gbc);

        JTextField contactField = new JTextField(30);
        StyleConstants.styleTextField(contactField);
        gbc.gridx = 1;
        panel.add(contactField, gbc);

        // Load data if editing
        if (doctorId != null) {
            try {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM doctors WHERE id = ?");
                stmt.setInt(1, doctorId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    specialtyField.setText(rs.getString("specialty"));
                    contactField.setText(rs.getString("contact"));
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        StyleConstants.styleButton(saveButton);
        StyleConstants.styleSecondaryButton(cancelButton);

        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty() || specialtyField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Veuillez remplir tous les champs obligatoires",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                PreparedStatement stmt;
                if (doctorId == null) {
                    stmt = conn.prepareStatement(
                        "INSERT INTO doctors (name, specialty, contact) VALUES (?, ?, ?)");
                } else {
                    stmt = conn.prepareStatement(
                        "UPDATE doctors SET name = ?, specialty = ?, contact = ? WHERE id = ?");
                }

                stmt.setString(1, nameField.getText().trim());
                stmt.setString(2, specialtyField.getText().trim());
                stmt.setString(3, contactField.getText().trim());
                if (doctorId != null) {
                    stmt.setInt(4, doctorId);
                }

                stmt.executeUpdate();
                stmt.close();

                dialog.dispose();
                loadDoctors();
                loadSpecialties();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'enregistrement: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String type) {
            setOpaque(true);
            if (type.equals("Supprimer")) {
                setBackground(new Color(220, 53, 69));
                setForeground(Color.WHITE);
        } else {
                StyleConstants.styleSecondaryButton(this);
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String type;

        public ButtonEditor(JCheckBox checkBox, String type) {
            super(checkBox);
            this.type = type;
            button = new JButton();
            if (type.equals("Supprimer")) {
                button.setBackground(new Color(220, 53, 69));
                button.setForeground(Color.WHITE);
            } else {
                StyleConstants.styleSecondaryButton(button);
            }
            button.addActionListener(e -> {
                fireEditingStopped();
                int row = doctorTable.getSelectedRow();
                if (row >= 0) {
                    int doctorId = (Integer) doctorTable.getValueAt(row, 0);
                    if (type.equals("Modifier")) {
                        showDoctorDialog(doctorId);
                    } else if (type.equals("Supprimer")) {
                        deleteDoctor(doctorId);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            button.setText(value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    private void deleteDoctor(int doctorId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce médecin ?\nCette action est irréversible.",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseConnection.beginTransaction();
                try {
                    // Vérifier s'il y a des rendez-vous associés
                    PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM appointments WHERE doctor_id = ?");
                    checkStmt.setInt(1, doctorId);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    int appointmentCount = rs.getInt(1);
                    checkStmt.close();

                    if (appointmentCount > 0) {
                        throw new SQLException("Ce médecin a " + appointmentCount + 
                            " rendez-vous associés. Veuillez d'abord gérer ces rendez-vous.");
                    }

                    // Supprimer le médecin
                    PreparedStatement deleteStmt = conn.prepareStatement(
                        "DELETE FROM doctors WHERE id = ?");
                    deleteStmt.setInt(1, doctorId);
                    int result = deleteStmt.executeUpdate();
                    deleteStmt.close();

                    if (result > 0) {
                        DatabaseConnection.commitTransaction();
                        JOptionPane.showMessageDialog(this,
                            "Médecin supprimé avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadDoctors();
                        loadSpecialties();
                    } else {
                        throw new SQLException("La suppression a échoué");
                    }
                } catch (SQLException ex) {
                    DatabaseConnection.rollbackTransaction();
                    throw ex;
                }
            } catch (SQLException e) {
                String errorMessage = "Erreur lors de la suppression du médecin: " + e.getMessage();
                System.err.println(errorMessage);
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    errorMessage,
                    "Erreur de base de données",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
