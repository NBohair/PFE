package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class FolderAllergiesPanel extends JPanel {
    private int folderId;
    private Connection conn;
    private JTable allergiesTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat;

    public FolderAllergiesPanel(int folderId) {
        this.folderId = folderId;
        this.conn = DatabaseConnection.getConnection();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadAllergies();
    }

    private void initComponents() {
        // Table
        String[] columns = {"Allergène", "Sévérité", "Réaction", "Date d'ajout", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        allergiesTable = new JTable(tableModel);
        allergiesTable.setRowHeight(35);
        allergiesTable.setShowGrid(true);
        allergiesTable.setGridColor(new Color(230, 230, 230));
        allergiesTable.getTableHeader().setReorderingAllowed(false);
        allergiesTable.getTableHeader().setBackground(new Color(240, 240, 240));
        allergiesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < allergiesTable.getColumnCount() - 1; i++) {
            allergiesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Action column
        allergiesTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        allergiesTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(allergiesTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Ajouter une allergie");
        StyleConstants.styleButton(addButton);
        addButton.addActionListener(e -> addAllergy());
        buttonsPanel.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadAllergies() {
        tableModel.setRowCount(0);
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM allergies WHERE folder_id = ? ORDER BY created_at DESC");
            stmt.setInt(1, folderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("allergen"),
                    rs.getString("severity"),
                    rs.getString("reaction"),
                    dateFormat.format(rs.getTimestamp("created_at")),
                    "Modifier"
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des allergies: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addAllergy() {
        showAllergyDialog("Ajouter une allergie", null, -1);
    }

    private void editAllergy(int row) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM allergies WHERE folder_id = ? LIMIT 1 OFFSET ?");
            stmt.setInt(1, folderId);
            stmt.setInt(2, row);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                showAllergyDialog("Modifier l'allergie", rs, row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement de l'allergie: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAllergyDialog(String title, ResultSet rs, int row) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            title, true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Allergen
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Allergène:"), gbc);

        JTextField allergenField = new JTextField(30);
        StyleConstants.styleTextField(allergenField);
        gbc.gridx = 1;
        panel.add(allergenField, gbc);

        // Severity
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Sévérité:"), gbc);

        String[] severities = {"légère", "modérée", "sévère"};
        JComboBox<String> severityCombo = new JComboBox<>(severities);
        StyleConstants.styleComboBox(severityCombo);
        gbc.gridx = 1;
        panel.add(severityCombo, gbc);

        // Reaction
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Réaction:"), gbc);

        JTextArea reactionArea = new JTextArea(4, 30);
        reactionArea.setLineWrap(true);
        reactionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(reactionArea);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);

        // Load data if editing
        try {
            if (rs != null) {
                allergenField.setText(rs.getString("allergen"));
                severityCombo.setSelectedItem(rs.getString("severity"));
                reactionArea.setText(rs.getString("reaction"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog,
                "Erreur lors du chargement des données de l'allergie: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
            dialog.dispose();
            return;
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        StyleConstants.styleButton(saveButton);
        StyleConstants.styleSecondaryButton(cancelButton);

        saveButton.addActionListener(e -> {
            if (allergenField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Veuillez saisir l'allergène",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                PreparedStatement stmt;
                if (row < 0) {
                    stmt = conn.prepareStatement(
                        "INSERT INTO allergies (folder_id, allergen, severity, reaction) " +
                        "VALUES (?, ?, ?, ?)");
                } else {
                    stmt = conn.prepareStatement(
                        "UPDATE allergies SET allergen = ?, severity = ?, reaction = ? " +
                        "WHERE folder_id = ? AND id = ?");
                }

                int paramIndex = 1;
                if (row < 0) {
                    stmt.setInt(paramIndex++, folderId);
                }
                stmt.setString(paramIndex++, allergenField.getText().trim());
                stmt.setString(paramIndex++, (String) severityCombo.getSelectedItem());
                stmt.setString(paramIndex++, reactionArea.getText().trim());
                if (row >= 0) {
                    stmt.setInt(paramIndex++, folderId);
                    stmt.setInt(paramIndex, rs.getInt("id"));
                }

                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(dialog,
                    "Allergie " + (row < 0 ? "ajoutée" : "modifiée") + " avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadAllergies();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'" + (row < 0 ? "ajout" : "modification") + " de l'allergie: " + ex.getMessage(),
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
        public ButtonRenderer() {
            setOpaque(true);
            StyleConstants.styleSecondaryButton(this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Modifier");
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            StyleConstants.styleSecondaryButton(button);
            button.addActionListener(e -> {
                fireEditingStopped();
                int row = allergiesTable.getSelectedRow();
                if (row >= 0) {
                    editAllergy(row);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            button.setText("Modifier");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Modifier";
        }
    }
} 