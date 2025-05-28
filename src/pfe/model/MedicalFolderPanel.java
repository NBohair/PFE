package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;
import java.util.Vector;

public class MedicalFolderPanel extends JPanel {
    private JTable folderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private Connection conn;
    private SimpleDateFormat dateFormat;

    private class ComboItem {
        private int id;
        private String display;

        public ComboItem(int id, String display) {
            this.id = id;
            this.display = display;
        }

        public int getId() { return id; }

        @Override
        public String toString() { return display; }
    }

    public MedicalFolderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        conn = DatabaseConnection.getConnection();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        initComponents();
        loadFolders();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Dossiers Médicaux");
        add(headerPanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(StyleConstants.PANEL_BORDER);

        // Search field
        searchField = new JTextField(20);
        StyleConstants.styleTextField(searchField);
        searchField.setPreferredSize(new Dimension(200, 30));

        // Status filter
        String[] statuses = {"Tous les statuts", "actif", "archivé", "urgent"};
        statusFilter = new JComboBox<>(statuses);
        StyleConstants.styleComboBox(statusFilter);

        // Search button
        JButton searchButton = new JButton("Rechercher");
        StyleConstants.styleButton(searchButton);
        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(new JLabel("Rechercher:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Statut:"));
        searchPanel.add(statusFilter);
        searchPanel.add(searchButton);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(StyleConstants.PANEL_BORDER);

        // Create table
        String[] columns = {"ID", "Patient", "Dernière visite", "Statut", "Documents", "Antécédents", "Traitements", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
            }
        };

        folderTable = new JTable(tableModel);
        folderTable.setRowHeight(35);
        folderTable.setShowGrid(true);
        folderTable.setGridColor(new Color(230, 230, 230));
        folderTable.getTableHeader().setReorderingAllowed(false);
        folderTable.getTableHeader().setBackground(new Color(240, 240, 240));
        folderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < folderTable.getColumnCount() - 1; i++) {
            folderTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Configure action column
        folderTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        folderTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(folderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton newFolderButton = new JButton("Nouveau dossier");
        StyleConstants.styleButton(newFolderButton);
        newFolderButton.addActionListener(e -> createNewFolder());

        buttonsPanel.add(newFolderButton);

        // Add panels
        add(searchPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadFolders() {
        tableModel.setRowCount(0);
        try {
            String query = "SELECT * FROM v_medical_folders";
            String status = (String) statusFilter.getSelectedItem();
            String search = searchField.getText().trim();
            
            if (!status.equals("Tous les statuts") || !search.isEmpty()) {
                query += " WHERE 1=1";
                if (!status.equals("Tous les statuts")) {
                    query += " AND status = ?";
                }
                if (!search.isEmpty()) {
                    query += " AND (patient_name LIKE ? OR id LIKE ?)";
                }
            }
            
            System.out.println("Executing query: " + query); // Debug log
            
            PreparedStatement stmt = conn.prepareStatement(query);
            int paramIndex = 1;
            
            if (!status.equals("Tous les statuts")) {
                stmt.setString(paramIndex++, status);
                System.out.println("Status parameter: " + status); // Debug log
            }
            if (!search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex, searchPattern);
                System.out.println("Search pattern: " + searchPattern); // Debug log
            }

            ResultSet rs = stmt.executeQuery();
            int rowCount = 0;
            
            while (rs.next()) {
                rowCount++;
                String lastVisitDate = rs.getDate("last_visit_date") != null ? 
                    dateFormat.format(rs.getDate("last_visit_date")) : "Non définie";
                    
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("patient_name"),
                    lastVisitDate,
                    rs.getString("status"),
                    rs.getInt("document_count"),
                    rs.getInt("history_count"),
                    rs.getInt("treatment_count"),
                    "Gérer"
                };
                tableModel.addRow(row);
            }
            
            System.out.println("Loaded " + rowCount + " folders"); // Debug log
            
            if (rowCount == 0) {
                // Optionally show a message if no results found
                JOptionPane.showMessageDialog(this,
                    "Aucun dossier trouvé avec les critères de recherche actuels",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage()); // Debug log
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des dossiers: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Force table to refresh
        tableModel.fireTableDataChanged();
    }

    private void performSearch() {
        loadFolders();
    }

    private void createNewFolder() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Nouveau Dossier Médical", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Patient selection
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Patient:"), gbc);
        
        JComboBox<ComboItem> patientCombo = createPatientComboBox();
        gbc.gridx = 1;
        panel.add(patientCombo, gbc);

        // Notes
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Notes:"), gbc);

        JTextArea notesArea = new JTextArea(5, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(notesArea);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        StyleConstants.styleButton(saveButton);
        StyleConstants.styleSecondaryButton(cancelButton);

        saveButton.addActionListener(e -> {
            ComboItem selectedPatient = (ComboItem) patientCombo.getSelectedItem();
            if (selectedPatient != null && selectedPatient.getId() != 0) {
                try {
                    CallableStatement stmt = conn.prepareCall("{CALL create_medical_folder(?, ?)}");
                    stmt.setInt(1, selectedPatient.getId());
                    stmt.setString(2, notesArea.getText().trim());
                    stmt.execute();
                    
                    JOptionPane.showMessageDialog(dialog,
                        "Dossier médical créé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    dialog.dispose();
                    loadFolders();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de la création du dossier: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Veuillez sélectionner un patient",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JComboBox<ComboItem> createPatientComboBox() {
        Vector<ComboItem> patients = new Vector<>();
        patients.add(new ComboItem(0, "Sélectionner un patient"));
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name FROM patients ORDER BY name");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                patients.add(new ComboItem(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des patients: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
        
        JComboBox<ComboItem> combo = new JComboBox<>(patients);
        StyleConstants.styleComboBox(combo);
        return combo;
    }

    // Button renderer for the actions column
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            StyleConstants.styleSecondaryButton(this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Gérer");
            return this;
        }
    }

    // Button editor for the actions column
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            StyleConstants.styleSecondaryButton(button);
            button.addActionListener(e -> {
                fireEditingStopped();
                int row = folderTable.getSelectedRow();
                if (row >= 0) {
                    openFolderDetails(row);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            button.setText("Gérer");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Gérer";
        }
    }

    private void openFolderDetails(int row) {
        int folderId = (Integer) folderTable.getValueAt(row, 0);
        String patientName = (String) folderTable.getValueAt(row, 1);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Dossier Médical - " + patientName, true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Informations", new FolderInfoPanel(folderId));
        tabbedPane.addTab("Documents", new FolderDocumentsPanel(folderId));
        tabbedPane.addTab("Antécédents", new FolderHistoryPanel(folderId));
        tabbedPane.addTab("Traitements", new FolderTreatmentsPanel(folderId));
        tabbedPane.addTab("Allergies", new FolderAllergiesPanel(folderId));

        dialog.add(tabbedPane);
        dialog.setVisible(true);
    }
} 