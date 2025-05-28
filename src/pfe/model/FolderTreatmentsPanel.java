package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;

public class FolderTreatmentsPanel extends JPanel {
    private int folderId;
    private Connection conn;
    private JTable treatmentsTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat;

    public FolderTreatmentsPanel(int folderId) {
        this.folderId = folderId;
        this.conn = DatabaseConnection.getConnection();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadTreatments();
    }

    private void initComponents() {
        // Table
        String[] columns = {"Médicament", "Dosage", "Fréquence", "Date début", "Date fin", "Notes", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        treatmentsTable = new JTable(tableModel);
        treatmentsTable.setRowHeight(35);
        treatmentsTable.setShowGrid(true);
        treatmentsTable.setGridColor(new Color(230, 230, 230));
        treatmentsTable.getTableHeader().setReorderingAllowed(false);
        treatmentsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        treatmentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < treatmentsTable.getColumnCount() - 1; i++) {
            treatmentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Action column
        treatmentsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        treatmentsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(treatmentsTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Ajouter un traitement");
        StyleConstants.styleButton(addButton);
        addButton.addActionListener(e -> addTreatment());
        buttonsPanel.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadTreatments() {
        tableModel.setRowCount(0);
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM current_treatments WHERE folder_id = ? ORDER BY start_date DESC");
            stmt.setInt(1, folderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("medication"),
                    rs.getString("dosage"),
                    rs.getString("frequency"),
                    dateFormat.format(rs.getDate("start_date")),
                    rs.getDate("end_date") != null ? dateFormat.format(rs.getDate("end_date")) : "",
                    rs.getString("notes"),
                    "Modifier"
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des traitements: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTreatment() {
        showTreatmentDialog("Ajouter un traitement", null, -1);
    }

    private void editTreatment(int row) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM current_treatments WHERE folder_id = ? LIMIT 1 OFFSET ?");
            stmt.setInt(1, folderId);
            stmt.setInt(2, row);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                showTreatmentDialog("Modifier le traitement", rs, row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement du traitement: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTreatmentDialog(String title, ResultSet rs, int row) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            title, true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Medication
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Médicament:"), gbc);

        JTextField medicationField = new JTextField(30);
        StyleConstants.styleTextField(medicationField);
        gbc.gridx = 1;
        panel.add(medicationField, gbc);

        // Dosage
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Dosage:"), gbc);

        JTextField dosageField = new JTextField(30);
        StyleConstants.styleTextField(dosageField);
        gbc.gridx = 1;
        panel.add(dosageField, gbc);

        // Frequency
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Fréquence:"), gbc);

        JTextField frequencyField = new JTextField(30);
        StyleConstants.styleTextField(frequencyField);
        gbc.gridx = 1;
        panel.add(frequencyField, gbc);

        // Start date
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Date début:"), gbc);

        JDateChooser startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("dd/MM/yyyy");
        gbc.gridx = 1;
        panel.add(startDateChooser, gbc);

        // End date
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Date fin:"), gbc);

        JDateChooser endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("dd/MM/yyyy");
        gbc.gridx = 1;
        panel.add(endDateChooser, gbc);

        // Notes
        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(new JLabel("Notes:"), gbc);

        JTextArea notesArea = new JTextArea(4, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(notesArea);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);

        // Load data if editing
        try {
            if (rs != null) {
                medicationField.setText(rs.getString("medication"));
                dosageField.setText(rs.getString("dosage"));
                frequencyField.setText(rs.getString("frequency"));
                startDateChooser.setDate(rs.getDate("start_date"));
                if (rs.getDate("end_date") != null) {
                    endDateChooser.setDate(rs.getDate("end_date"));
                }
                notesArea.setText(rs.getString("notes"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        StyleConstants.styleButton(saveButton);
        StyleConstants.styleSecondaryButton(cancelButton);

        saveButton.addActionListener(e -> {
            if (medicationField.getText().trim().isEmpty() || 
                dosageField.getText().trim().isEmpty() ||
                frequencyField.getText().trim().isEmpty() ||
                startDateChooser.getDate() == null) {
                
                JOptionPane.showMessageDialog(dialog,
                    "Veuillez remplir tous les champs obligatoires",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                PreparedStatement stmt;
                if (row < 0) {
                    stmt = conn.prepareStatement(
                        "INSERT INTO current_treatments (folder_id, medication, dosage, frequency, " +
                        "start_date, end_date, notes) VALUES (?, ?, ?, ?, ?, ?, ?)");
                } else {
                    stmt = conn.prepareStatement(
                        "UPDATE current_treatments SET medication = ?, dosage = ?, frequency = ?, " +
                        "start_date = ?, end_date = ?, notes = ? WHERE folder_id = ? AND id = ?");
                }

                int paramIndex = 1;
                if (row < 0) {
                    stmt.setInt(paramIndex++, folderId);
                }
                stmt.setString(paramIndex++, medicationField.getText().trim());
                stmt.setString(paramIndex++, dosageField.getText().trim());
                stmt.setString(paramIndex++, frequencyField.getText().trim());
                stmt.setDate(paramIndex++, new java.sql.Date(startDateChooser.getDate().getTime()));
                if (endDateChooser.getDate() != null) {
                    stmt.setDate(paramIndex++, new java.sql.Date(endDateChooser.getDate().getTime()));
                } else {
                    stmt.setNull(paramIndex++, Types.DATE);
                }
                stmt.setString(paramIndex++, notesArea.getText().trim());
                if (row >= 0) {
                    stmt.setInt(paramIndex++, folderId);
                    stmt.setInt(paramIndex, rs.getInt("id"));
                }

                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(dialog,
                    "Traitement " + (row < 0 ? "ajouté" : "modifié") + " avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadTreatments();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'" + (row < 0 ? "ajout" : "modification") + " du traitement: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridy = 6;
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
                int row = treatmentsTable.getSelectedRow();
                if (row >= 0) {
                    editTreatment(row);
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