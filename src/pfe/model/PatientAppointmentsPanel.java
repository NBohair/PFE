package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class PatientAppointmentsPanel extends JPanel {
    private int patientId;
    private Connection conn;
    private JTable appointmentsTable;

    public PatientAppointmentsPanel(int patientId) {
        this.patientId = patientId;
        this.conn = DatabaseConnection.getConnection();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // Create table
        String[] columns = {"Date", "Heure", "Médecin", "Motif", "Statut"};
        appointmentsTable = new JTable(new DefaultTableModel(columns, 0));
        styleTable(appointmentsTable);

        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);
        add(scrollPane, BorderLayout.CENTER);

        // Load appointments
        loadAppointments();
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
        int[] columnWidths = {100, 80, 150, 300, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    private void loadAppointments() {
        DefaultTableModel model = (DefaultTableModel) appointmentsTable.getModel();
        model.setRowCount(0);

        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT r.date_time, d.name as doctor_name, r.reason, r.status " +
                "FROM rendezvous r " +
                "JOIN doctors d ON r.doctor_id = d.id " +
                "WHERE r.patient_id = ? " +
                "ORDER BY r.date_time DESC");
            stmt.setInt(1, patientId);

            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            while (rs.next()) {
                Timestamp dateTime = rs.getTimestamp("date_time");
                model.addRow(new Object[]{
                    dateFormat.format(dateTime),
                    timeFormat.format(dateTime),
                    rs.getString("doctor_name"),
                    rs.getString("reason"),
                    rs.getString("status")
                });
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des rendez-vous: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 