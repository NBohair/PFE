package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;

public class FolderHistoryPanel extends JPanel {
    private int folderId;
    private Connection conn;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat;

    public FolderHistoryPanel(int folderId) {
        this.folderId = folderId;
        this.conn = DatabaseConnection.getConnection();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadHistory();
    }

    private void initComponents() {
        // Table
        String[] columns = {"Date", "Condition", "Description", "Statut", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(35);
        historyTable.setShowGrid(true);
        historyTable.setGridColor(new Color(230, 230, 230));
        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.getTableHeader().setBackground(new Color(240, 240, 240));
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < historyTable.getColumnCount() - 1; i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Action column
        historyTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        historyTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Ajouter un antécédent");
        StyleConstants.styleButton(addButton);
        addButton.addActionListener(e -> addHistory());
        buttonsPanel.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadHistory() {
        tableModel.setRowCount(0);
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM medical_history WHERE folder_id = ? ORDER BY date_diagnosed DESC");
            stmt.setInt(1, folderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    dateFormat.format(rs.getDate("date_diagnosed")),
                    rs.getString("condition_type"),
                    rs.getString("description"),
                    rs.getString("status"),
                    "Modifier"
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des antécédents: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addHistory() {
        showHistoryDialog("Ajouter un antécédent", null, -1);
    }

    private void editHistory(int row) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM medical_history WHERE folder_id = ? LIMIT 1 OFFSET ?");
            stmt.setInt(1, folderId);
            stmt.setInt(2, row);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                showHistoryDialog("Modifier l'antécédent", rs, row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement de l'antécédent: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHistoryDialog(String title, ResultSet rs, int row) {
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

        // Condition
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Condition:"), gbc);

        JTextField conditionField = new JTextField(30);
        StyleConstants.styleTextField(conditionField);
        gbc.gridx = 1;
        panel.add(conditionField, gbc);

        // Date
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Date:"), gbc);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        gbc.gridx = 1;
        panel.add(dateChooser, gbc);

        // Status
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Statut:"), gbc);

        String[] statuses = {"actif", "résolu", "chronique"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        StyleConstants.styleComboBox(statusCombo);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

        // Description
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Description:"), gbc);

        JTextArea descArea = new JTextArea(4, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descArea);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);

        // Load data if editing
        try {
            if (rs != null) {
                conditionField.setText(rs.getString("condition_type"));
                dateChooser.setDate(rs.getDate("date_diagnosed"));
                statusCombo.setSelectedItem(rs.getString("status"));
                descArea.setText(rs.getString("description"));
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
            if (conditionField.getText().trim().isEmpty() || dateChooser.getDate() == null) {
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
                        "INSERT INTO medical_history (folder_id, condition_type, date_diagnosed, status, description) " +
                        "VALUES (?, ?, ?, ?, ?)");
                } else {
                    stmt = conn.prepareStatement(
                        "UPDATE medical_history SET condition_type = ?, date_diagnosed = ?, status = ?, description = ? " +
                        "WHERE folder_id = ? AND id = ?");
                }

                int paramIndex = 1;
                if (row < 0) {
                    stmt.setInt(paramIndex++, folderId);
                }
                stmt.setString(paramIndex++, conditionField.getText().trim());
                stmt.setDate(paramIndex++, new java.sql.Date(dateChooser.getDate().getTime()));
                stmt.setString(paramIndex++, (String) statusCombo.getSelectedItem());
                stmt.setString(paramIndex++, descArea.getText().trim());
                if (row >= 0) {
                    stmt.setInt(paramIndex++, folderId);
                    stmt.setInt(paramIndex, rs.getInt("id"));
                }

                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(dialog,
                    "Antécédent " + (row < 0 ? "ajouté" : "modifié") + " avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadHistory();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'" + (row < 0 ? "ajout" : "modification") + " de l'antécédent: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridy = 4;
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
                int row = historyTable.getSelectedRow();
                if (row >= 0) {
                    editHistory(row);
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