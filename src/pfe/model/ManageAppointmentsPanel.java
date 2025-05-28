package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class ManageAppointmentsPanel extends JPanel {
    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusCombo;
    private JComboBox<ComboItem> doctorCombo;
    private Calendar currentDate;
    private Connection conn;

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

    public ManageAppointmentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        currentDate = Calendar.getInstance();
        conn = DatabaseConnection.getConnection();
        initComponents();
        loadAppointments();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Gestion des Rendez-vous");
        add(headerPanel, BorderLayout.NORTH);

        // Search and Filter Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(StyleConstants.PANEL_BORDER);

        // Search field
        searchField = new JTextField(20);
        StyleConstants.styleTextField(searchField);
        searchField.setPreferredSize(new Dimension(200, 30));

        // Status filter
        String[] statuses = {"Tous les statuts", "scheduled", "completed", "canceled"};
        statusCombo = new JComboBox<>(statuses);
        StyleConstants.styleComboBox(statusCombo);

        // Doctor filter
        doctorCombo = createDoctorComboBox();

        // Search button
        JButton searchButton = new JButton("Rechercher");
        StyleConstants.styleButton(searchButton);
        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(new JLabel("Rechercher:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Statut:"));
        searchPanel.add(statusCombo);
        searchPanel.add(new JLabel("Médecin:"));
        searchPanel.add(doctorCombo);
        searchPanel.add(searchButton);

        // Calendar Navigation Panel
        JPanel calendarNavPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        calendarNavPanel.setBackground(Color.WHITE);
        calendarNavPanel.setBorder(StyleConstants.PANEL_BORDER);

        JButton prevButton = new JButton("◀");
        JLabel dateLabel = new JLabel(new SimpleDateFormat("MMMM yyyy").format(currentDate.getTime()));
        dateLabel.setFont(StyleConstants.TITLE_FONT);
        JButton nextButton = new JButton("▶");
        JButton todayButton = new JButton("Aujourd'hui");

        StyleConstants.styleSecondaryButton(prevButton);
        StyleConstants.styleSecondaryButton(nextButton);
        StyleConstants.styleButton(todayButton);

        prevButton.addActionListener(e -> changeMonth(-1));
        nextButton.addActionListener(e -> changeMonth(1));
        todayButton.addActionListener(e -> goToToday());

        calendarNavPanel.add(prevButton);
        calendarNavPanel.add(dateLabel);
        calendarNavPanel.add(nextButton);
        calendarNavPanel.add(Box.createHorizontalStrut(20));
        calendarNavPanel.add(todayButton);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = createButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JComboBox<ComboItem> createDoctorComboBox() {
        Vector<ComboItem> doctors = new Vector<>();
        doctors.add(new ComboItem(0, "Tous les médecins"));
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name, specialty FROM doctors ORDER BY name");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String display = rs.getString("name") + " - " + rs.getString("specialty");
                doctors.add(new ComboItem(rs.getInt("id"), display));
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des médecins: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
        
        JComboBox<ComboItem> combo = new JComboBox<>(doctors);
        StyleConstants.styleComboBox(combo);
        return combo;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(StyleConstants.PANEL_BORDER);

        // Créer le modèle de table avec les colonnes
        String[] columns = {"Date", "Heure", "Patient", "Médecin", "Motif", "Statut", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Seule la colonne Actions est éditable
            }
        };

        appointmentsTable = new JTable(model);
        appointmentsTable.setRowHeight(35);
        appointmentsTable.setShowGrid(true);
        appointmentsTable.setGridColor(new Color(230, 230, 230));
        appointmentsTable.getTableHeader().setReorderingAllowed(false);
        appointmentsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        appointmentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Configurer les largeurs des colonnes
        int[] columnWidths = {100, 80, 150, 200, 150, 100, 100};
        TableColumnModel columnModel = appointmentsTable.getColumnModel();
        for (int i = 0; i < columnWidths.length; i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
            columnModel.getColumn(i).setMinWidth(columnWidths[i]);
        }

        // Centrer le contenu des cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < appointmentsTable.getColumnCount() - 1; i++) {
            appointmentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Renderer personnalisé pour la colonne Actions
        appointmentsTable.getColumnModel().getColumn(6).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JButton button = new JButton("Gérer");
                button.setBackground(new Color(51, 122, 183));
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
                return button;
            }
        });

        // Editor personnalisé pour la colonne Actions
        appointmentsTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JButton button;

            {
                button = new JButton("Gérer");
                button.setBackground(new Color(51, 122, 183));
                button.setForeground(Color.WHITE);
                button.addActionListener(e -> {
                    fireEditingStopped();
                    int row = appointmentsTable.getSelectedRow();
                    if (row >= 0) {
                        manageAppointment(row);
                    }
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                return button;
            }
        });

        loadAppointments();

        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadAppointments() {
        DefaultTableModel model = (DefaultTableModel) appointmentsTable.getModel();
        model.setRowCount(0);

        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT r.date_time, p.name as patient_name, d.name as doctor_name, " +
                "r.reason, r.status " +
                "FROM rendezvous r " +
                "JOIN patients p ON r.patient_id = p.id " +
                "JOIN doctors d ON r.doctor_id = d.id " +
                "ORDER BY r.date_time DESC");

            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            while (rs.next()) {
                Timestamp dateTime = rs.getTimestamp("date_time");
                String date = dateFormat.format(dateTime);
                String time = timeFormat.format(dateTime);
                String patientName = rs.getString("patient_name");
                String doctorName = rs.getString("doctor_name");
                String reason = rs.getString("reason");
                String status = rs.getString("status");

                model.addRow(new Object[]{
                    date,
                    time,
                    patientName,
                    doctorName,
                    reason,
                    status,
                    "Gérer"
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

    private void performSearch() {
        loadAppointments();
    }

    private void changeMonth(int delta) {
        currentDate.add(Calendar.MONTH, delta);
        updateDateLabel();
        loadAppointments();
    }

    private void goToToday() {
        currentDate = Calendar.getInstance();
        updateDateLabel();
        loadAppointments();
    }

    private void updateDateLabel() {
        Container contentPanel = (Container) getComponent(1);
        Container calendarPanel = (Container) contentPanel.getComponent(1);
        JLabel dateLabel = (JLabel) calendarPanel.getComponent(1);
        dateLabel.setText(new SimpleDateFormat("MMMM yyyy").format(currentDate.getTime()));
    }

    private void manageAppointment(int row) {
        String date = (String) appointmentsTable.getValueAt(row, 0);
        String time = (String) appointmentsTable.getValueAt(row, 1);
        String patient = (String) appointmentsTable.getValueAt(row, 2);
        String doctor = (String) appointmentsTable.getValueAt(row, 3);
        String status = (String) appointmentsTable.getValueAt(row, 5);
        
        // Créer une boîte de dialogue personnalisée
        JDialog dialog = new JDialog();
        dialog.setTitle("Gérer le rendez-vous");
        dialog.setModal(true);
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        // Panel principal avec bordure et padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Panel d'informations
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 10));
        infoPanel.setBackground(Color.WHITE);

        // Style des labels
        Font labelFont = new Font("Arial", Font.BOLD, 13);
        Font valueFont = new Font("Arial", Font.PLAIN, 13);

        // Ajouter les informations avec un meilleur style
        addStyledInfo(infoPanel, "Date", date, labelFont, valueFont);
        addStyledInfo(infoPanel, "Heure", time, labelFont, valueFont);
        addStyledInfo(infoPanel, "Patient", patient, labelFont, valueFont);
        addStyledInfo(infoPanel, "Médecin", doctor, labelFont, valueFont);
        addStyledInfo(infoPanel, "Statut", status, labelFont, valueFont);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Bouton Annuler avec icône
        JButton cancelButton = new JButton("Annuler le rendez-vous");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Bouton Fermer avec style
        JButton closeButton = new JButton("Fermer");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Action pour annuler le rendez-vous
        cancelButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(dialog,
                "Êtes-vous sûr de vouloir annuler ce rendez-vous ?",
                "Confirmation d'annulation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE rendezvous SET status = 'canceled' " +
                        "WHERE DATE(date_time) = ? AND TIME_FORMAT(TIME(date_time), '%H:%i') = ? " +
                        "AND doctor_id = (SELECT id FROM doctors WHERE name = ?) " +
                        "AND patient_id = (SELECT id FROM patients WHERE name = ?)");
                    
                    stmt.setString(1, date);
                    stmt.setString(2, time);
                    stmt.setString(3, doctor);
                    stmt.setString(4, patient);
                    
                    int result = stmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(dialog,
                            "Le rendez-vous a été annulé avec succès.",
                            "Confirmation",
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadAppointments();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Impossible d'annuler le rendez-vous. Veuillez réessayer.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de l'annulation du rendez-vous: " + ex.getMessage(),
                        "Erreur de base de données",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void addStyledInfo(JPanel panel, String label, String value, Font labelFont, Font valueFont) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rowPanel.setBackground(Color.WHITE);
        
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setFont(labelFont);
        labelComponent.setPreferredSize(new Dimension(80, 20));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(valueFont);
        
        rowPanel.add(labelComponent);
        rowPanel.add(valueComponent);
        
        panel.add(rowPanel);
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(StyleConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton exportButton = new JButton("Exporter");
        JButton printButton = new JButton("Imprimer");
        JButton newAppointmentButton = new JButton("Nouveau rendez-vous");

        StyleConstants.styleSecondaryButton(exportButton);
        StyleConstants.styleSecondaryButton(printButton);
        StyleConstants.styleButton(newAppointmentButton);

        exportButton.addActionListener(e -> exportAppointments());
        printButton.addActionListener(e -> printAppointments());
        newAppointmentButton.addActionListener(e -> openNewAppointment());

        panel.add(exportButton);
        panel.add(printButton);
        panel.add(newAppointmentButton);

        return panel;
    }

    private void exportAppointments() {
        // TODO: Implémenter l'exportation
        JOptionPane.showMessageDialog(this,
            "Fonctionnalité d'exportation à implémenter",
            "Export",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void printAppointments() {
        // TODO: Implémenter l'impression
        JOptionPane.showMessageDialog(this,
            "Fonctionnalité d'impression à implémenter",
            "Impression",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void openNewAppointment() {
        JFrame appointmentFrame = new JFrame("Nouveau rendez-vous");
        appointmentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        appointmentFrame.setSize(800, 600);
        appointmentFrame.setLocationRelativeTo(null);
        appointmentFrame.setContentPane(new AppointmentPanel());
        appointmentFrame.setVisible(true);
    }
} 