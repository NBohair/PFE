package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ManagePatients extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private Connection conn;
    private JTextField searchField;

    public ManagePatients() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        conn = DatabaseConnection.getConnection();
        initComponents();
        loadPatients();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Gestion des Patients");
        add(headerPanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(StyleConstants.PANEL_BORDER);

        searchField = new JTextField(20);
        StyleConstants.styleTextField(searchField);
        searchField.setPreferredSize(new Dimension(200, 30));

        JButton searchButton = new JButton("Rechercher");
        StyleConstants.styleButton(searchButton);
        searchButton.addActionListener(e -> loadPatients());

        searchPanel.add(new JLabel("Rechercher:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Nom", "Date de naissance", "Contact", "Actions", "Supprimer"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(35);
        patientTable.setShowGrid(true);
        patientTable.setGridColor(new Color(230, 230, 230));
        patientTable.getTableHeader().setReorderingAllowed(false);
        patientTable.getTableHeader().setBackground(new Color(240, 240, 240));
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < patientTable.getColumnCount() - 2; i++) {
            patientTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Action columns
        patientTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("Modifier"));
        patientTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), "Modifier"));
        patientTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("Supprimer"));
        patientTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), "Supprimer"));

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton addButton = new JButton("Ajouter un patient");
        StyleConstants.styleButton(addButton);
        addButton.addActionListener(e -> showPatientDialog(null));

        buttonsPanel.add(addButton);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadPatients() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT * FROM patients WHERE 1=1";
            String search = searchField.getText().trim();

            if (!search.isEmpty()) {
                query += " AND (name LIKE ? OR contact LIKE ?)";
            }
            query += " ORDER BY name";

            PreparedStatement stmt = conn.prepareStatement(query);
            if (!search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDate("birth_date"),
                    rs.getString("contact"),
                    "Modifier",
                    "Supprimer"
                };
                tableModel.addRow(row);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des patients: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPatientDialog(Integer patientId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            patientId == null ? "Ajouter un patient" : "Modifier le patient", true);
        dialog.setSize(400, 350);
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

        // Birth date field
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Date de naissance:"), gbc);

        com.toedter.calendar.JDateChooser birthDateChooser = new com.toedter.calendar.JDateChooser();
        birthDateChooser.setDateFormatString("yyyy-MM-dd");
        gbc.gridx = 1;
        panel.add(birthDateChooser, gbc);

        // Contact field
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Contact:"), gbc);

        JTextField contactField = new JTextField(30);
        StyleConstants.styleTextField(contactField);
        gbc.gridx = 1;
        panel.add(contactField, gbc);

        // Load data if editing
        if (patientId != null) {
            try {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM patients WHERE id = ?");
                stmt.setInt(1, patientId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    birthDateChooser.setDate(rs.getDate("birth_date"));
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
            if (nameField.getText().trim().isEmpty() || birthDateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(dialog,
                    "Veuillez remplir tous les champs obligatoires",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                PreparedStatement stmt;
                if (patientId == null) {
                    stmt = conn.prepareStatement(
                        "INSERT INTO patients (name, birth_date, contact) VALUES (?, ?, ?)");
                } else {
                    stmt = conn.prepareStatement(
                        "UPDATE patients SET name = ?, birth_date = ?, contact = ? WHERE id = ?");
                }

                stmt.setString(1, nameField.getText().trim());
                stmt.setDate(2, new java.sql.Date(birthDateChooser.getDate().getTime()));
                stmt.setString(3, contactField.getText().trim());
                if (patientId != null) {
                    stmt.setInt(4, patientId);
                }

                stmt.executeUpdate();
                stmt.close();

                dialog.dispose();
                loadPatients();
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
                int row = patientTable.getSelectedRow();
                if (row >= 0) {
                    int patientId = (Integer) patientTable.getValueAt(row, 0);
                    if (type.equals("Modifier")) {
                        showPatientDialog(patientId);
                    } else if (type.equals("Supprimer")) {
                        deletePatient(patientId);
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

    private void deletePatient(int patientId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce patient ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM patients WHERE id = ?");
                stmt.setInt(1, patientId);
                stmt.executeUpdate();
                stmt.close();
                loadPatients();
                JOptionPane.showMessageDialog(this,
                    "Le patient a été supprimé avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression du patient: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
