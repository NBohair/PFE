package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class PatientDiagnosticsPanel extends JPanel {
    private int patientId;
    private Connection conn;
    private JTable diagnosticsTable;

    public PatientDiagnosticsPanel(int patientId) {
        this.patientId = patientId;
        this.conn = DatabaseConnection.getConnection();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // Create table
        String[] columns = {"Date", "Médecin", "Diagnostic"};
        diagnosticsTable = new JTable(new DefaultTableModel(columns, 0));
        styleTable(diagnosticsTable);

        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(diagnosticsTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);
        add(scrollPane, BorderLayout.CENTER);

        // Load diagnostics
        loadDiagnostics();
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells except description
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Date
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Doctor

        // Left align description
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer); // Description

        // Set column widths
        int[] columnWidths = {100, 150, 500};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Make description column wrap text
        table.getColumnModel().getColumn(2).setCellRenderer(new TextAreaRenderer());
    }

    private void loadDiagnostics() {
        DefaultTableModel model = (DefaultTableModel) diagnosticsTable.getModel();
        model.setRowCount(0);

        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT d.date_time, doc.name as doctor_name, d.diagnosis " +
                "FROM diagnostics d " +
                "JOIN doctors doc ON d.doctor_id = doc.id " +
                "WHERE d.patient_id = ? " +
                "ORDER BY d.date_time DESC");
            stmt.setInt(1, patientId);

            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                Date date = rs.getDate("date_time");
                model.addRow(new Object[]{
                    dateFormat.format(date),
                    rs.getString("doctor_name"),
                    rs.getString("diagnosis")
                });
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des diagnostics: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom renderer for wrapping text in the description column
    private class TextAreaRenderer extends JTextArea implements TableCellRenderer {
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setFont(table.getFont());
            
            // Calculate preferred height
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            int textHeight = getPreferredSize().height;
            int rowHeight = Math.max(textHeight + 4, table.getRowHeight());
            
            if (table.getRowHeight(row) != rowHeight) {
                table.setRowHeight(row, rowHeight);
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            return this;
        }
    }
} 