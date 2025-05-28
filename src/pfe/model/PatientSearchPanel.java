package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PatientSearchPanel extends JPanel {
    private JTextField searchField;
    private JTable resultTable;
    private JComboBox<String> searchTypeCombo;
    private Connection conn;
    private Timer searchTimer;

    public PatientSearchPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        conn = DatabaseConnection.getConnection();
        initComponents();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Recherche de Patients");

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(StyleConstants.PANEL_BORDER);

        // Search Type Combo
        String[] searchTypes = {"Nom", "ID", "Contact", "Email"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        StyleConstants.styleComboBox(searchTypeCombo);

        // Search Field
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        StyleConstants.styleTextField(searchField);

        // Add search delay for better performance
        searchTimer = new Timer(300, e -> performSearch());
        searchTimer.setRepeats(false);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { startSearchTimer(); }
            public void removeUpdate(DocumentEvent e) { startSearchTimer(); }
            public void insertUpdate(DocumentEvent e) { startSearchTimer(); }
        });

        JButton clearButton = new JButton("Effacer");
        StyleConstants.styleSecondaryButton(clearButton);
        clearButton.addActionListener(e -> {
            searchField.setText("");
            performSearch();
        });

        searchPanel.add(new JLabel("Rechercher par:"));
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchField);
        searchPanel.add(clearButton);

        // Results Table
        String[] columns = {"ID", "Nom", "Contact", "Email", "Adresse"};
        resultTable = new JTable(new DefaultTableModel(columns, 0));
        styleTable(resultTable);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);

        // Add double-click listener to open patient details
        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openPatientDetails();
                }
            }
        });

        // Layout
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Initial search
        performSearch();
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        int[] columnWidths = {50, 150, 100, 150, 300};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    private void startSearchTimer() {
        if (searchTimer.isRunning()) {
            searchTimer.restart();
        } else {
            searchTimer.start();
        }
    }

    private void performSearch() {
        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0);

        String searchText = searchField.getText().trim();
        String searchType = (String) searchTypeCombo.getSelectedItem();

        try {
            String query = "SELECT id, name, contact, email, address FROM patients WHERE 1=1";
            
            if (!searchText.isEmpty()) {
                switch (searchType) {
                    case "ID":
                        query += " AND id LIKE ?";
                        break;
                    case "Nom":
                        query += " AND name LIKE ?";
                        break;
                    case "Contact":
                        query += " AND contact LIKE ?";
                        break;
                    case "Email":
                        query += " AND email LIKE ?";
                        break;
                }
            }
            
            query += " ORDER BY name LIMIT 100";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            
            if (!searchText.isEmpty()) {
                stmt.setString(1, "%" + searchText + "%");
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getString("email"),
                    rs.getString("address")
                });
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche : " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPatientDetails() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow >= 0) {
            int patientId = (Integer) resultTable.getValueAt(selectedRow, 0);
            String patientName = (String) resultTable.getValueAt(selectedRow, 1);

            // Create patient details window
            JFrame detailsFrame = new JFrame("Détails du Patient - " + patientName);
            detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            detailsFrame.setSize(800, 600);
            detailsFrame.setLocationRelativeTo(null);

            // Create tabs for different sections
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Informations", new PatientInfoPanel(patientId));
            tabbedPane.addTab("Rendez-vous", new PatientAppointmentsPanel(patientId));
            tabbedPane.addTab("Diagnostics", new PatientDiagnosticsPanel(patientId));

            detailsFrame.add(tabbedPane);
            detailsFrame.setVisible(true);
        }
    }
} 