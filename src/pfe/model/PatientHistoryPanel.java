package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.border.EmptyBorder;

public class PatientHistoryPanel extends JPanel {
    private JComboBox<ComboItem> patientCombo;
    private JTable historyTable;
    private Connection conn;
    private static final Color HEADER_COLOR = new Color(0, 102, 204);
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 12);

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

    public PatientHistoryPanel() {
        conn = DatabaseConnection.getConnection();
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JLabel titleLabel = new JLabel("Historique des Patients");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel patientLabel = new JLabel("Patient:");
        patientLabel.setFont(HEADER_FONT);
        patientCombo = createPatientComboBox();
        patientCombo.setPreferredSize(new Dimension(200, 30));
        ((JLabel)patientCombo.getRenderer()).setFont(CONTENT_FONT);

        JButton updateButton = new JButton("Actualiser");
        updateButton.setFont(CONTENT_FONT);
        updateButton.setBackground(HEADER_COLOR);
        updateButton.setForeground(Color.WHITE);
        updateButton.setBorderPainted(false);
        updateButton.setFocusPainted(false);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateButton.addActionListener(e -> updateHistory());

        searchPanel.add(patientLabel);
        searchPanel.add(patientCombo);
        searchPanel.add(updateButton);

        // Table
        String[] columns = {"Date", "Type", "Médecin", "Description", "Statut"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(model);
        historyTable.setFont(CONTENT_FONT);
        historyTable.setRowHeight(30);
        historyTable.setShowGrid(true);
        historyTable.setGridColor(new Color(230, 230, 230));
        historyTable.getTableHeader().setFont(HEADER_FONT);
        historyTable.getTableHeader().setBackground(new Color(240, 240, 240));
        historyTable.getTableHeader().setPreferredSize(new Dimension(100, 35));

        // Personnaliser le rendu des cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(232, 242, 254));
                    c.setForeground(HEADER_COLOR);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 249, 249));
                    c.setForeground(Color.BLACK);
                }
                setBorder(new EmptyBorder(0, 5, 0, 5));
                setHorizontalAlignment(column == 3 ? LEFT : CENTER);
                return c;
            }
        };

        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Définir les largeurs des colonnes
        int[] columnWidths = {150, 100, 200, 300, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            historyTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JComboBox<ComboItem> createPatientComboBox() {
        DefaultComboBoxModel<ComboItem> model = new DefaultComboBoxModel<>();
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name FROM patients ORDER BY name");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addElement(new ComboItem(
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
        
        JComboBox<ComboItem> combo = new JComboBox<>(model);
        combo.setBackground(Color.WHITE);
        return combo;
    }

    private void updateHistory() {
        ComboItem selectedPatient = (ComboItem) patientCombo.getSelectedItem();
        if (selectedPatient == null) return;

        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);

        try {
            // Récupérer les rendez-vous
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.date_time, a.type, d.name as doctor_name, " +
                "a.description, a.status, 'appointment' as record_type " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE a.patient_id = ? " +
                "UNION ALL " +
                "SELECT mr.date_time, 'Consultation' as type, d.name as doctor_name, " +
                "CONCAT('Diagnostic: ', mr.diagnosis, '\nTraitement: ', mr.treatment) as description, " +
                "'completed' as status, 'medical_record' as record_type " +
                "FROM medical_records mr " +
                "JOIN doctors d ON mr.doctor_id = d.id " +
                "WHERE mr.patient_id = ? " +
                "ORDER BY date_time DESC");
            
            stmt.setInt(1, selectedPatient.getId());
            stmt.setInt(2, selectedPatient.getId());
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (rs.next()) {
                String description = rs.getString("description");
                if (description != null) {
                    description = description.replace("\n", "<br>");
                }
                
                model.addRow(new Object[]{
                    dateFormat.format(rs.getTimestamp("date_time")),
                    rs.getString("type"),
                    rs.getString("doctor_name"),
                    String.format("<html>%s</html>", description),
                    capitalizeFirstLetter(rs.getString("status"))
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la mise à jour de l'historique: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
} 