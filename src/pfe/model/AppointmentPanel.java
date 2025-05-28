package pfe.model;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.Calendar;

public class AppointmentPanel extends JPanel {
    private JComboBox<ComboItem> patientCombo;
    private JComboBox<ComboItem> doctorCombo;
    private JComboBox<String> timeCombo;
    private JDateChooser dateChooser;
    private JSpinner motifSpinner;
    private Connection conn;
    private JPanel availabilityPanel;

    // Helper class for ComboBox items
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

    public AppointmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        conn = DatabaseConnection.getConnection();
        initComponents();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Nouveau Rendez-vous");

        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(StyleConstants.PANEL_BORDER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Patient selection
        addFormField(formPanel, "Patient:", createPatientComboBox(), gbc, 0);
        
        // Doctor selection
        addFormField(formPanel, "Médecin:", createDoctorComboBox(), gbc, 1);
        
        // Date field with JCalendar
        initDateChooser();
        addFormField(formPanel, "Date:", dateChooser, gbc, 2);

        // Time selection
        addFormField(formPanel, "Heure:", createTimeComboBox(), gbc, 3);

        // Reason section
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel reasonLabel = new JLabel("Motif:");
        reasonLabel.setFont(StyleConstants.SUBTITLE_FONT);
        formPanel.add(reasonLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        String[] motifs = {"Consultation", "Suivi", "Urgence", "Contrôle", "Vaccination"};
        SpinnerListModel motifModel = new SpinnerListModel(motifs);
        motifSpinner = new JSpinner(motifModel);
        JSpinner.ListEditor editor = new JSpinner.ListEditor(motifSpinner);
        motifSpinner.setEditor(editor);
        motifSpinner.setPreferredSize(new Dimension(200, 30));
        formPanel.add(motifSpinner, gbc);

        // Add form to content panel
        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Available Slots Panel
        JPanel slotsPanel = createSlotsPanel();
        contentPanel.add(slotsPanel, BorderLayout.EAST);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton checkAvailabilityButton = new JButton("Vérifier disponibilité");
        JButton confirmButton = new JButton("Confirmer");
        JButton cancelButton = new JButton("Annuler");

        StyleConstants.styleSecondaryButton(checkAvailabilityButton);
        StyleConstants.styleButton(confirmButton);
        StyleConstants.styleSecondaryButton(cancelButton);

        checkAvailabilityButton.addActionListener(e -> checkAvailability());
        confirmButton.addActionListener(e -> confirmAppointment());
        cancelButton.addActionListener(e -> clearForm());

        buttonsPanel.add(checkAvailabilityButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(confirmButton);

        // Add all panels
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Add listeners
        doctorCombo.addActionListener(e -> checkAvailability());
        dateChooser.addPropertyChangeListener("date", e -> checkAvailability());
    }

    private JPanel createSlotsPanel() {
        JPanel slotsPanel = new JPanel(new BorderLayout(10, 10));
        slotsPanel.setBackground(Color.WHITE);
        slotsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 20, 0, 0)
        ));

        JLabel titleLabel = new JLabel("Créneaux Disponibles");
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        slotsPanel.add(titleLabel, BorderLayout.NORTH);

        // Create morning and afternoon sections
        JPanel timeGrid = new JPanel(new GridLayout(2, 1, 0, 10));
        timeGrid.setBackground(Color.WHITE);

        // Morning slots
        JPanel morningPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        morningPanel.setBackground(Color.WHITE);
        morningPanel.setBorder(BorderFactory.createTitledBorder("Matin"));
        
        String[] morningTimes = {"09:00", "09:30", "10:00", "10:30", "11:00", "11:30"};
        for (String time : morningTimes) {
            morningPanel.add(createTimeSlot(time));
        }

        // Afternoon slots
        JPanel afternoonPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        afternoonPanel.setBackground(Color.WHITE);
        afternoonPanel.setBorder(BorderFactory.createTitledBorder("Après-midi"));
        
        String[] afternoonTimes = {"14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};
        for (String time : afternoonTimes) {
            afternoonPanel.add(createTimeSlot(time));
        }

        timeGrid.add(morningPanel);
        timeGrid.add(afternoonPanel);

        JScrollPane scrollPane = new JScrollPane(timeGrid);
        scrollPane.setBorder(null);
        slotsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBackground(Color.WHITE);
        
        addLegendItem(legendPanel, new Color(46, 204, 113), "Disponible");
        addLegendItem(legendPanel, new Color(231, 76, 60), "Occupé");
        
        slotsPanel.add(legendPanel, BorderLayout.SOUTH);

        return slotsPanel;
    }

    private JPanel createTimeSlot(String time) {
        JPanel slot = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        slot.setBackground(Color.WHITE);
        slot.setName(time);

        JLabel statusDot = new JLabel("●");
        statusDot.setForeground(new Color(46, 204, 113));
        statusDot.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(StyleConstants.REGULAR_FONT);

        slot.add(statusDot);
        slot.add(timeLabel);

        slot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (statusDot.getForeground().equals(new Color(46, 204, 113))) {
                    timeCombo.setSelectedItem(time);
                }
            }
        });

        return slot;
    }

    private void addLegendItem(JPanel panel, Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(Color.WHITE);

        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        dot.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel label = new JLabel(text);
        label.setFont(StyleConstants.REGULAR_FONT);

        item.add(dot);
        item.add(label);
        panel.add(item);
    }

    private JComboBox<ComboItem> createPatientComboBox() {
        Vector<ComboItem> patients = new Vector<>();
        patients.add(new ComboItem(0, "Sélectionner un patient"));
        
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM patients ORDER BY name");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                patients.add(new ComboItem(rs.getInt("id"), rs.getString("name")));
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
        
        patientCombo = new JComboBox<>(patients);
        StyleConstants.styleComboBox(patientCombo);
        return patientCombo;
    }

    private JComboBox<ComboItem> createDoctorComboBox() {
        Vector<ComboItem> doctors = new Vector<>();
        doctors.add(new ComboItem(0, "Sélectionner un médecin"));
        
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
        
        doctorCombo = new JComboBox<>(doctors);
        StyleConstants.styleComboBox(doctorCombo);
        return doctorCombo;
    }

    private JComboBox<String> createTimeComboBox() {
        Vector<String> availableTimes = new Vector<>();
        availableTimes.add("Sélectionner une heure");
        
        // Ajouter les créneaux en fonction de l'heure actuelle
        updateAvailableTimeSlots(availableTimes);
        
        timeCombo = new JComboBox<>(availableTimes);
        StyleConstants.styleComboBox(timeCombo);
        
        // Mettre à jour les créneaux quand la date change
        dateChooser.addPropertyChangeListener("date", e -> updateTimeComboBox());
        
        return timeCombo;
    }

    private void updateTimeComboBox() {
        String selectedTime = (String) timeCombo.getSelectedItem();
        Vector<String> availableTimes = new Vector<>();
        availableTimes.add("Sélectionner une heure");
        
        updateAvailableTimeSlots(availableTimes);
        
        timeCombo.setModel(new DefaultComboBoxModel<>(availableTimes));
        
        // Restaurer la sélection si elle est toujours valide
        if (availableTimes.contains(selectedTime)) {
            timeCombo.setSelectedItem(selectedTime);
        }
    }

    private void updateAvailableTimeSlots(Vector<String> availableTimes) {
        String[] allTimeSlots = {
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
        };
        
        // Obtenir la date et l'heure actuelles
        Calendar now = Calendar.getInstance();
        Calendar selected = Calendar.getInstance();
        selected.setTime(dateChooser.getDate() != null ? dateChooser.getDate() : new Date());
        
        // Si c'est la date d'aujourd'hui, filtrer les créneaux passés
        boolean isToday = selected.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                         selected.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                         selected.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH);
        
        for (String timeSlot : allTimeSlots) {
            String[] timeParts = timeSlot.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            
            if (isToday) {
                // Pour aujourd'hui, n'ajouter que les créneaux futurs
                if (hour > now.get(Calendar.HOUR_OF_DAY) || 
                    (hour == now.get(Calendar.HOUR_OF_DAY) && minute > now.get(Calendar.MINUTE))) {
                    availableTimes.add(timeSlot);
                }
            } else if (selected.after(now)) {
                // Pour les dates futures, ajouter tous les créneaux
                availableTimes.add(timeSlot);
            }
        }
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(StyleConstants.REGULAR_FONT);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
    }

    private void checkAvailability() {
        ComboItem selectedDoctor = (ComboItem) doctorCombo.getSelectedItem();
        if (selectedDoctor == null || selectedDoctor.getId() == 0 || dateChooser.getDate() == null) {
            return;
        }

        try {
            String selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate());
            
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT TIME_FORMAT(TIME(date_time), '%H:%i') as time " +
                "FROM rendezvous " +
                "WHERE doctor_id = ? AND DATE(date_time) = ? AND status != 'canceled'");
            stmt.setInt(1, selectedDoctor.getId());
            stmt.setString(2, selectedDate);
            
            ResultSet rs = stmt.executeQuery();
            Vector<String> bookedTimes = new Vector<>();
            while (rs.next()) {
                bookedTimes.add(rs.getString("time"));
            }
            
            rs.close();
            stmt.close();

            // Update time slots display
            JPanel contentPanel = (JPanel) getComponent(1);
            JPanel slotsPanel = (JPanel) contentPanel.getComponent(1);
            JScrollPane scrollPane = (JScrollPane) slotsPanel.getComponent(1);
            JPanel timeGrid = (JPanel) scrollPane.getViewport().getView();

            for (Component section : timeGrid.getComponents()) {
                if (section instanceof JPanel) {
                    JPanel sectionPanel = (JPanel) section;
                    for (Component slot : sectionPanel.getComponents()) {
                        if (slot instanceof JPanel) {
                            JPanel timeSlot = (JPanel) slot;
                            String time = timeSlot.getName();
                            JLabel statusDot = (JLabel) timeSlot.getComponent(0);
                            statusDot.setForeground(bookedTimes.contains(time) ? 
                                new Color(231, 76, 60) :    // Booked
                                new Color(46, 204, 113));   // Available
                        }
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la vérification des disponibilités: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmAppointment() {
        ComboItem selectedPatient = (ComboItem) patientCombo.getSelectedItem();
        ComboItem selectedDoctor = (ComboItem) doctorCombo.getSelectedItem();
        String selectedTime = (String) timeCombo.getSelectedItem();
        String motif = motifSpinner.getValue().toString();
        Date selectedDate = dateChooser.getDate();
        
        // Validation de base
        if (selectedPatient == null || selectedPatient.getId() == 0 ||
            selectedDoctor == null || selectedDoctor.getId() == 0 ||
            "Sélectionner une heure".equals(selectedTime) ||
            selectedDate == null) {
            
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs obligatoires",
                "Champs requis",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérification de la date et l'heure
        try {
            // Obtenir la date et l'heure actuelles
            Date now = new Date();
            Calendar calNow = Calendar.getInstance();
            calNow.setTime(now);

            // Configurer la date et l'heure sélectionnées
            Calendar calSelected = Calendar.getInstance();
            calSelected.setTime(selectedDate);
            String[] timeParts = selectedTime.split(":");
            calSelected.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            calSelected.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
            calSelected.set(Calendar.SECOND, 0);

            // Vérifier si la date est dans le passé
            if (calSelected.before(calNow)) {
                JOptionPane.showMessageDialog(this,
                    "Impossible de créer un rendez-vous dans le passé.\nVeuillez sélectionner une date et une heure futures.",
                    "Date invalide",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Si tout est valide, procéder à l'insertion
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calSelected.getTime());
            
            // Check if slot is already booked
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM rendezvous WHERE doctor_id = ? AND date_time = ? AND status != 'canceled'");
            checkStmt.setInt(1, selectedDoctor.getId());
            checkStmt.setString(2, dateTime);
            
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                    "Ce créneau n'est plus disponible. Veuillez en choisir un autre.",
                    "Créneau indisponible",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Insert new appointment
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO rendezvous (patient_id, doctor_id, date_time, reason, status) VALUES (?, ?, ?, ?, 'scheduled')");
            insertStmt.setInt(1, selectedPatient.getId());
            insertStmt.setInt(2, selectedDoctor.getId());
            insertStmt.setString(3, dateTime);
            insertStmt.setString(4, motif);
            
            insertStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Rendez-vous confirmé avec succès!",
                "Confirmation",
                JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création du rendez-vous: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        patientCombo.setSelectedIndex(0);
        doctorCombo.setSelectedIndex(0);
        timeCombo.setSelectedIndex(0);
        motifSpinner.setValue(motifSpinner.getModel().getValue());
        dateChooser.setDate(new Date());
    }

    // Ajouter une méthode pour restreindre la sélection de date
    private void initDateChooser() {
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date());
        dateChooser.setDateFormatString("yyyy-MM-dd");
        
        // Définir la date minimum comme aujourd'hui
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        dateChooser.setMinSelectableDate(cal.getTime());
        
        // Ajouter un PropertyChangeListener pour valider la date
        dateChooser.addPropertyChangeListener("date", e -> {
            if (dateChooser.getDate() != null) {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.setTime(dateChooser.getDate());
                selectedCal.set(Calendar.HOUR_OF_DAY, 0);
                selectedCal.set(Calendar.MINUTE, 0);
                selectedCal.set(Calendar.SECOND, 0);
                selectedCal.set(Calendar.MILLISECOND, 0);

                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                if (selectedCal.before(today)) {
                    dateChooser.setDate(today.getTime());
                    JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner une date à partir d'aujourd'hui.",
                        "Date invalide",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
            // Mettre à jour les créneaux horaires quand la date change
            updateTimeComboBox();
        });
    }
} 