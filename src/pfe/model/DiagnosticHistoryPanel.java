package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.util.Vector;
import java.text.ParseException;

public class DiagnosticHistoryPanel extends JPanel {
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private Connection conn;
    private JTextField searchField;
    private JComboBox<String> doctorCombo;
    private JTextField dateFromField;
    private JTextField dateToField;
    private SimpleDateFormat dateFormat;

    public DiagnosticHistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        conn = DatabaseConnection.getConnection();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        initComponents();
        loadDoctors();
        loadDiagnostics();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header Panel avec le titre
        JPanel headerPanel = new JPanel(new BorderLayout());
        StyleConstants.styleHeaderPanel(headerPanel, "Historique des Diagnostics");
        
        // Search Panel avec GridBagLayout pour une meilleure organisation
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(StyleConstants.PANEL_BORDER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Recherche
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        searchPanel.add(new JLabel("Rechercher:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        searchField = new JTextField(20);
        StyleConstants.styleTextField(searchField);
        searchPanel.add(searchField, gbc);
        
        // Médecin
        gbc.gridx = 2;
        gbc.weightx = 0;
        searchPanel.add(new JLabel("Médecin:"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        doctorCombo = new JComboBox<>();
        doctorCombo.setPreferredSize(new Dimension(200, 25));
        StyleConstants.styleComboBox(doctorCombo);
        searchPanel.add(doctorCombo, gbc);
        
        // Date range
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        searchPanel.add(new JLabel("Du:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        dateFromField = new JTextField(10);
        dateFromField.setText(dateFormat.format(new Date())); // Date du jour
        searchPanel.add(dateFromField, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0;
        searchPanel.add(new JLabel("Au:"), gbc);
        
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        dateToField = new JTextField(10);
        dateToField.setText(dateFormat.format(new Date())); // Date du jour
        searchPanel.add(dateToField, gbc);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(Color.WHITE);
        
        JButton searchButton = new JButton("Rechercher");
        StyleConstants.styleButton(searchButton);
        searchButton.addActionListener(e -> loadDiagnostics());
        
        JButton resetButton = new JButton("Réinitialiser");
        StyleConstants.styleSecondaryButton(resetButton);
        resetButton.addActionListener(e -> resetSearch());
        
        buttonsPanel.add(resetButton);
        buttonsPanel.add(searchButton);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        searchPanel.add(buttonsPanel, gbc);

        // Add search panel to header
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Patient", "Médecin", "Date", "Actions"};
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

        // Configurer les largeurs de colonnes en pourcentage
        TableColumnModel columnModel = historyTable.getColumnModel();
        int[] columnWidths = {10, 30, 30, 15, 15}; // Pourcentages
        
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
            
            // Centre toutes les colonnes sauf Actions
            if (i != 4) {
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                column.setCellRenderer(centerRenderer);
            }
        }

        // Action column
        columnModel.getColumn(4).setCellRenderer(new ButtonRenderer());
        columnModel.getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Wrap table in scroll pane with responsive borders
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Add table panel to center with margins
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
    }

    private void loadDoctors() {
        doctorCombo.addItem("Tous les médecins");
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT DISTINCT d.name FROM doctors d " +
                "JOIN diagnostics di ON d.id = di.doctor_id " +
                "ORDER BY d.name");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                doctorCombo.addItem(rs.getString("name"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDiagnostics() {
        tableModel.setRowCount(0);
        try {
            // Préparer les dates
            Date dateFrom = dateFormat.parse(dateFromField.getText());
            Date dateTo = dateFormat.parse(dateToField.getText());
            
            String query = "SELECT d.id, p.name as patient_name, doc.name as doctor_name, " +
                          "d.date_time, d.diagnosis " +
                          "FROM diagnostics d " +
                          "LEFT JOIN patients p ON d.patient_id = p.id " +
                          "LEFT JOIN doctors doc ON d.doctor_id = doc.id " +
                          "WHERE d.date_time BETWEEN ? AND ? ";

            // Ajouter la condition du médecin sélectionné
            String selectedDoctor = (String) doctorCombo.getSelectedItem();
            if (!"Tous les médecins".equals(selectedDoctor)) {
                query += "AND doc.name = ? ";
            }

            // Ajouter la condition de recherche si nécessaire
            String search = searchField.getText().trim();
            if (!search.isEmpty()) {
                query += "AND (p.name LIKE ? OR doc.name LIKE ? OR d.diagnosis LIKE ?) ";
            }

            query += "ORDER BY d.date_time DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            int paramIndex = 1;

            // Définir les paramètres de date
            stmt.setDate(paramIndex++, new java.sql.Date(dateFrom.getTime()));
            stmt.setDate(paramIndex++, new java.sql.Date(dateTo.getTime()));

            // Définir le paramètre du médecin si nécessaire
            if (!"Tous les médecins".equals(selectedDoctor)) {
                stmt.setString(paramIndex++, selectedDoctor);
            }

            // Définir les paramètres de recherche si nécessaire
            if (!search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    dateFormat.format(rs.getDate("date_time")),
                    "Voir détails"
                };
                tableModel.addRow(row);
            }
            rs.close();
            stmt.close();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des diagnostics: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetSearch() {
        searchField.setText("");
        doctorCombo.setSelectedIndex(0);
        dateFromField.setText(dateFormat.format(new Date()));
        dateToField.setText(dateFormat.format(new Date()));
        loadDiagnostics();
    }

    private void showDiagnosticDetails(int diagnosticId) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT d.*, p.name as patient_name, doc.name as doctor_name, doc.specialty " +
                "FROM diagnostics d " +
                "JOIN patients p ON d.patient_id = p.id " +
                "JOIN doctors doc ON d.doctor_id = doc.id " +
                "WHERE d.id = ?");
            stmt.setInt(1, diagnosticId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Détails du Diagnostic", true);
                dialog.setSize(600, 500);
                dialog.setLocationRelativeTo(this);

                JPanel panel = new JPanel(new BorderLayout(10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                // Info Panel
                JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
                infoPanel.add(new JLabel("Patient:"));
                infoPanel.add(new JLabel(rs.getString("patient_name")));
                infoPanel.add(new JLabel("Médecin:"));
                infoPanel.add(new JLabel(rs.getString("doctor_name") + " - " + rs.getString("specialty")));
                infoPanel.add(new JLabel("Date:"));
                infoPanel.add(new JLabel(new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("date_time"))));

                // Content Panel
                JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
                
                // Symptoms
                JPanel symptomsPanel = new JPanel(new BorderLayout());
                symptomsPanel.add(new JLabel("Symptômes:"), BorderLayout.NORTH);
                JTextArea symptomsArea = new JTextArea(rs.getString("symptoms"));
                symptomsArea.setEditable(false);
                symptomsArea.setLineWrap(true);
                symptomsArea.setWrapStyleWord(true);
                symptomsPanel.add(new JScrollPane(symptomsArea), BorderLayout.CENTER);
                
                // Diagnostic
                JPanel diagnosticPanel = new JPanel(new BorderLayout());
                diagnosticPanel.add(new JLabel("Diagnostic:"), BorderLayout.NORTH);
                JTextArea diagnosticArea = new JTextArea(rs.getString("diagnosis"));
                diagnosticArea.setEditable(false);
                diagnosticArea.setLineWrap(true);
                diagnosticArea.setWrapStyleWord(true);
                diagnosticPanel.add(new JScrollPane(diagnosticArea), BorderLayout.CENTER);
                
                // Treatment
                JPanel treatmentPanel = new JPanel(new BorderLayout());
                treatmentPanel.add(new JLabel("Traitement:"), BorderLayout.NORTH);
                JTextArea treatmentArea = new JTextArea(rs.getString("treatment"));
                treatmentArea.setEditable(false);
                treatmentArea.setLineWrap(true);
                treatmentArea.setWrapStyleWord(true);
                treatmentPanel.add(new JScrollPane(treatmentArea), BorderLayout.CENTER);

                contentPanel.add(symptomsPanel);
                contentPanel.add(diagnosticPanel);
                contentPanel.add(treatmentPanel);

                panel.add(infoPanel, BorderLayout.NORTH);
                panel.add(contentPanel, BorderLayout.CENTER);

                JButton closeButton = new JButton("Fermer");
                StyleConstants.styleButton(closeButton);
                closeButton.addActionListener(e -> dialog.dispose());

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(closeButton);
                panel.add(buttonPanel, BorderLayout.SOUTH);

                dialog.add(panel);
                dialog.setVisible(true);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des détails du diagnostic: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            StyleConstants.styleSecondaryButton(this);
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

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            StyleConstants.styleSecondaryButton(button);
            button.addActionListener(e -> {
                fireEditingStopped();
                int row = historyTable.getSelectedRow();
                if (row >= 0) {
                    int diagnosticId = (Integer) historyTable.getValueAt(row, 0);
                    showDiagnosticDetails(diagnosticId);
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
} 