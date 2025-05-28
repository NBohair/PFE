package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DoctorSchedulePanel extends JPanel {
    private JComboBox<ComboItem> doctorCombo;
    private JTable scheduleTable;
    private Calendar currentDate;
    private Connection conn;
    private JComboBox<String> specialtyFilter;
    private JCheckBox showOnlyAvailableCheckbox;
    private JComboBox<ComboItem> searchComboBox;
    private JComboBox<String> statusFilter;
    private JComboBox<String> typeFilter;
    private JPanel navigationPanel;
    private JLabel dateLabel;

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

    public DoctorSchedulePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        currentDate = Calendar.getInstance();
        conn = DatabaseConnection.getConnection();
        initComponents();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Planning des Médecins");
        add(headerPanel, BorderLayout.NORTH);

        // Main Filter Panel avec GridBagLayout pour un meilleur contrôle
        JPanel mainFilterPanel = new JPanel(new GridBagLayout());
        mainFilterPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel pour le médecin
        JPanel doctorPanel = new JPanel(new GridBagLayout());
        doctorPanel.setBackground(Color.WHITE);
        doctorPanel.setBorder(StyleConstants.PANEL_BORDER);

        // Sélection du médecin
        JLabel doctorLabel = new JLabel("Médecin:");
        doctorCombo = createDoctorComboBox();
        StyleConstants.styleComboBox(doctorCombo);
        doctorCombo.setPreferredSize(new Dimension(300, 30));
        doctorCombo.addActionListener(e -> updateSchedule());

        // Ajouter les composants au panel médecin
        GridBagConstraints gbcDoctor = new GridBagConstraints();
        gbcDoctor.gridx = 0;
        gbcDoctor.gridy = 0;
        gbcDoctor.insets = new Insets(5, 5, 5, 5);
        doctorPanel.add(doctorLabel, gbcDoctor);

        gbcDoctor.gridx = 1;
        gbcDoctor.weightx = 1.0;
        gbcDoctor.fill = GridBagConstraints.HORIZONTAL;
        doctorPanel.add(doctorCombo, gbcDoctor);

        // Panel pour les filtres
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(StyleConstants.PANEL_BORDER);

        // Composants de filtrage
        JLabel typeLabel = new JLabel("Type:");
        typeFilter = new JComboBox<>(new String[]{
            "Tous les types",
            "Consultation",
            "Suivi",
            "Urgence",
            "Contrôle"
        });
        StyleConstants.styleComboBox(typeFilter);

        JLabel statusLabel = new JLabel("Statut:");
        statusFilter = new JComboBox<>(new String[]{
            "Tous les statuts",
            "Disponible",
            "Confirmé",
            "En attente",
            "Annulé"
        });
        StyleConstants.styleComboBox(statusFilter);

        JLabel patientLabel = new JLabel("Filtrer par patient:");
        searchComboBox = createPatientComboBox();
        StyleConstants.styleComboBox(searchComboBox);
        searchComboBox.setPreferredSize(new Dimension(200, 30));

        // Ajouter les composants au panel des filtres avec GridBagLayout
        GridBagConstraints gbcFilter = new GridBagConstraints();
        gbcFilter.insets = new Insets(5, 5, 5, 5);
        gbcFilter.fill = GridBagConstraints.HORIZONTAL;

        // Première ligne
        gbcFilter.gridx = 0;
        gbcFilter.gridy = 0;
        filterPanel.add(typeLabel, gbcFilter);

        gbcFilter.gridx = 1;
        gbcFilter.weightx = 0.3;
        filterPanel.add(typeFilter, gbcFilter);

        gbcFilter.gridx = 2;
        gbcFilter.weightx = 0;
        filterPanel.add(statusLabel, gbcFilter);

        gbcFilter.gridx = 3;
        gbcFilter.weightx = 0.3;
        filterPanel.add(statusFilter, gbcFilter);

        gbcFilter.gridx = 4;
        gbcFilter.weightx = 0;
        filterPanel.add(patientLabel, gbcFilter);

        gbcFilter.gridx = 5;
        gbcFilter.weightx = 0.4;
        filterPanel.add(searchComboBox, gbcFilter);

        // Panel principal de navigation avec une taille fixe
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Panel pour la navigation de la date (plus compact)
        JPanel dateNavigationPanel = new JPanel();
        dateNavigationPanel.setLayout(new BoxLayout(dateNavigationPanel, BoxLayout.X_AXIS));
        dateNavigationPanel.setBackground(Color.WHITE);
        dateNavigationPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateNavigationPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        dateNavigationPanel.setMaximumSize(new Dimension(300, 30));

        JLabel leftArrow = new JLabel("←");
        leftArrow.setFont(new Font("Arial", Font.BOLD, 20));
        leftArrow.setForeground(new Color(0, 102, 204));
        leftArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftArrow.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                changeWeek(-1);
            }
            public void mouseEntered(MouseEvent e) {
                leftArrow.setForeground(new Color(0, 51, 153));
            }
            public void mouseExited(MouseEvent e) {
                leftArrow.setForeground(new Color(0, 102, 204));
            }
        });

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dateLabel.setForeground(new Color(0, 102, 204));

        JLabel rightArrow = new JLabel("→");
        rightArrow.setFont(new Font("Arial", Font.BOLD, 20));
        rightArrow.setForeground(new Color(0, 102, 204));
        rightArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightArrow.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                changeWeek(1);
            }
            public void mouseEntered(MouseEvent e) {
                rightArrow.setForeground(new Color(0, 51, 153));
            }
            public void mouseExited(MouseEvent e) {
                rightArrow.setForeground(new Color(0, 102, 204));
            }
        });

        // Ajouter les composants avec des espaces flexibles
        dateNavigationPanel.add(Box.createHorizontalGlue());
        dateNavigationPanel.add(leftArrow);
        dateNavigationPanel.add(Box.createHorizontalStrut(10));
        dateNavigationPanel.add(dateLabel);
        dateNavigationPanel.add(Box.createHorizontalStrut(10));
        dateNavigationPanel.add(rightArrow);
        dateNavigationPanel.add(Box.createHorizontalGlue());

        // Panel pour les boutons (plus compact)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(500, 35));

        JButton todayButton = new JButton("Aujourd'hui");
        JButton searchButton = new JButton("Appliquer les filtres");
        JButton resetButton = new JButton("Réinitialiser");

        StyleConstants.styleButton(todayButton);
        StyleConstants.styleButton(searchButton);
        StyleConstants.styleSecondaryButton(resetButton);

        todayButton.addActionListener(e -> goToToday());
        searchButton.addActionListener(e -> applyFilters());
        resetButton.addActionListener(e -> resetFilters());

        buttonPanel.add(todayButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(searchButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(resetButton);

        // Assembler les panels
        mainPanel.add(dateNavigationPanel);
        mainPanel.add(buttonPanel);

        // Panel principal de navigation
        navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(Color.WHITE);
        navigationPanel.add(mainPanel, BorderLayout.CENTER);
        navigationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        navigationPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));

        // Ajouter tous les panels au panel principal
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        mainFilterPanel.add(doctorPanel, gbc);

        gbc.gridy = 1;
        mainFilterPanel.add(filterPanel, gbc);

        gbc.gridy = 2;
        mainFilterPanel.add(navigationPanel, gbc);

        add(mainFilterPanel, BorderLayout.NORTH);

        // Table du planning
        scheduleTable = createScheduleTable();
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        // Initialiser la date au démarrage
        updateDateLabel();
    }

    private JTable createScheduleTable() {
        String[] columns = {"Heure", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};
        String[] times = {
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
        };
        
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (String time : times) {
            model.addRow(new Object[]{time, null, null, null, null, null});
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(60);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBackground(new Color(240, 240, 240));
                } else {
                    setBackground(Color.WHITE);
                }
                setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                return c;
            }
        });
        
        return table;
    }

    private JComboBox<ComboItem> createDoctorComboBox() {
        Vector<ComboItem> doctors = new Vector<>();
        doctors.add(new ComboItem(0, "Sélectionner un médecin"));
        
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name, specialty FROM doctors ORDER BY name");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                doctors.add(new ComboItem(
                    rs.getInt("id"),
                    rs.getString("name") + " - " + rs.getString("specialty")
                ));
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
        
        return new JComboBox<>(doctors);
    }

    private JComboBox<ComboItem> createPatientComboBox() {
        Vector<ComboItem> patients = new Vector<>();
        patients.add(new ComboItem(0, "Tous les patients"));
        
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

        // Créer un modèle de combo box personnalisé
        DefaultComboBoxModel<ComboItem> model = new DefaultComboBoxModel<>(patients);
        JComboBox<ComboItem> combo = new JComboBox<>(model);
        combo.setPreferredSize(new Dimension(200, 30));
        
        // Personnaliser l'apparence
        combo.setBackground(Color.WHITE);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Ajouter un renderer personnalisé pour l'affichage des items
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ComboItem) {
                    setText(((ComboItem) value).toString());
                }
                setPreferredSize(new Dimension(200, 25));
                return this;
            }
        });

        return combo;
    }

    private void updateDoctorList() {
        String selectedSpecialty = (String) specialtyFilter.getSelectedItem();
        
        try {
            String query = "SELECT id, name, specialty FROM doctors WHERE 1=1";
            if (selectedSpecialty != null && !selectedSpecialty.equals("Toutes les spécialités")) {
                query += " AND specialty = ?";
            }
            query += " ORDER BY name";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            if (selectedSpecialty != null && !selectedSpecialty.equals("Toutes les spécialités")) {
                stmt.setString(1, selectedSpecialty);
            }
            
            ResultSet rs = stmt.executeQuery();
            DefaultComboBoxModel<ComboItem> model = new DefaultComboBoxModel<>();
            model.addElement(new ComboItem(0, "Sélectionner un médecin"));
            
            while (rs.next()) {
                model.addElement(new ComboItem(
                    rs.getInt("id"),
                    rs.getString("name") + " - " + rs.getString("specialty")
                ));
            }
            
            doctorCombo.setModel(model);
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la mise à jour de la liste des médecins: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSchedule() {
        ComboItem selectedDoctor = (ComboItem) doctorCombo.getSelectedItem();
        if (selectedDoctor == null || selectedDoctor.getId() == 0) {
            clearSchedule();
            return;
        }

        try {
            // Get the start and end dates for the current week
            Calendar cal = (Calendar) currentDate.clone();
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            java.sql.Date weekStart = new java.sql.Date(cal.getTimeInMillis());
            cal.add(Calendar.DATE, 4);
            java.sql.Date weekEnd = new java.sql.Date(cal.getTimeInMillis());

            // Get appointments for the selected doctor in the current week
            String query = "SELECT DATE(date_time) as date, TIME_FORMAT(TIME(date_time), '%H:%i') as time, " +
                "p.name as patient_name, r.status, r.type " +
                "FROM rendezvous r " +
                "JOIN patients p ON r.patient_id = p.id " +
                "WHERE r.doctor_id = ? AND DATE(date_time) BETWEEN ? AND ? " +
                "ORDER BY date_time";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedDoctor.getId());
            stmt.setDate(2, weekStart);
            stmt.setDate(3, weekEnd);

            ResultSet rs = stmt.executeQuery();

            // Clear existing schedule
            clearSchedule();

            // Fill in appointments
            while (rs.next()) {
                updateTableWithAppointment(rs);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement du planning: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSchedule() {
        for (int row = 0; row < scheduleTable.getRowCount(); row++) {
            for (int col = 1; col < scheduleTable.getColumnCount(); col++) {
                scheduleTable.setValueAt(null, row, col);
            }
        }
    }

    private void changeWeek(int delta) {
        currentDate.add(Calendar.WEEK_OF_YEAR, delta);
        updateDateLabel();
        updateSchedule();
    }

    private void goToToday() {
        currentDate = Calendar.getInstance();
        // Ajuster à la semaine en cours
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        updateDateLabel();
        updateSchedule();
    }

    private void updateDateLabel() {
        if (currentDate == null) {
            currentDate = Calendar.getInstance();
            currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }
        
        Calendar cal = (Calendar) currentDate.clone();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date weekStart = cal.getTime();
        cal.add(Calendar.DATE, 4);
        Date weekEnd = cal.getTime();
        
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy");
        
        String dateText = String.format("%s-%s %s",
            dayFormat.format(weekStart),
            dayFormat.format(weekEnd),
            monthFormat.format(currentDate.getTime()));
            
        if (dateLabel != null) {
            dateLabel.setText(dateText);
        }
    }

    private void updateTableWithAppointment(ResultSet rs) throws SQLException {
        Date appointmentDate = rs.getDate("date");
        String time = rs.getString("time");
        String patientName = rs.getString("patient_name");
        String status = rs.getString("status");
        String type = rs.getString("type");

        Calendar appointmentCal = Calendar.getInstance();
        appointmentCal.setTime(appointmentDate);
        int dayOfWeek = appointmentCal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 1;

        for (int row = 0; row < scheduleTable.getRowCount(); row++) {
            if (scheduleTable.getValueAt(row, 0).equals(time)) {
                // Créer une cellule personnalisée avec HTML pour le formatage
                String displayText = String.format("<html><b>%s</b><br/><span style='color: #666;'>%s</span><br/><i>%s</i></html>", 
                    patientName, type, status);
                
                // Créer un renderer personnalisé pour cette cellule
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        setHorizontalAlignment(SwingConstants.CENTER);
                        
                        // Définir la couleur de fond selon le type
                        switch (type) {
                            case "Consultation":
                                setBackground(new Color(230, 240, 255)); // Bleu très clair
                                break;
                            case "Suivi":
                                setBackground(new Color(230, 255, 230)); // Vert très clair
                                break;
                            case "Urgence":
                                setBackground(new Color(255, 230, 230)); // Rouge très clair
                                break;
                            case "Contrôle":
                                setBackground(new Color(255, 255, 230)); // Jaune très clair
                                break;
                            default:
                                setBackground(Color.WHITE);
                        }
                        
                        // Ajouter une bordure pour séparer les cellules
                        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                        
                        return c;
                    }
                };
                
                scheduleTable.setValueAt(displayText, row, dayOfWeek);
                scheduleTable.getColumnModel().getColumn(dayOfWeek).setCellRenderer(renderer);
                break;
            }
        }
    }

    private void applyFilters() {
        ComboItem selectedPatient = (ComboItem) searchComboBox.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();
        String type = (String) typeFilter.getSelectedItem();
        ComboItem doctor = (ComboItem) doctorCombo.getSelectedItem();

        try {
            StringBuilder query = new StringBuilder(
                "SELECT DATE(r.date_time) as date, TIME_FORMAT(TIME(r.date_time), '%H:%i') as time, " +
                "p.name as patient_name, r.status, r.type " +
                "FROM rendezvous r " +
                "JOIN patients p ON r.patient_id = p.id " +
                "WHERE r.doctor_id = ? AND DATE(r.date_time) BETWEEN ? AND ? "
            );

            if (selectedPatient != null && selectedPatient.getId() != 0) {
                query.append("AND p.id = ? ");
            }
            if (!status.equals("Tous les statuts")) {
                query.append("AND r.status = ? ");
            }
            if (!type.equals("Tous les types")) {
                query.append("AND r.type = ? ");
            }

            query.append("ORDER BY r.date_time");

            // Clear existing schedule
            clearSchedule();

            if (doctor != null && doctor.getId() != 0) {
                PreparedStatement stmt = conn.prepareStatement(query.toString());
                int paramIndex = 1;
                
                stmt.setInt(paramIndex++, doctor.getId());
                
                Calendar cal = (Calendar) currentDate.clone();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                stmt.setDate(paramIndex++, new java.sql.Date(cal.getTimeInMillis()));
                
                cal.add(Calendar.DATE, 4);
                stmt.setDate(paramIndex++, new java.sql.Date(cal.getTimeInMillis()));

                if (selectedPatient != null && selectedPatient.getId() != 0) {
                    stmt.setInt(paramIndex++, selectedPatient.getId());
                }
                if (!status.equals("Tous les statuts")) {
                    stmt.setString(paramIndex++, status.toLowerCase());
                }
                if (!type.equals("Tous les types")) {
                    stmt.setString(paramIndex++, type);
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    updateTableWithAppointment(rs);
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'application des filtres: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFilters() {
        searchComboBox.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        typeFilter.setSelectedIndex(0);
        doctorCombo.setSelectedIndex(0);
        updateSchedule();
    }
} 