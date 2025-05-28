package pfe.model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import pfe.DB.RendezVousDAO; // Ensure correct import path
import pfe.service.RendezVous; // Ensure correct import path
import pfe.service.User; // Ensure correct import path
import com.toedter.calendar.JDateChooser; // Import JDateChooser

public class RendezVousPage extends JFrame {
    // Declare UI components
    private JComboBox<String> doctorCombo; // For selecting doctors
    private DefaultTableModel tableModel; // For the appointments table
    private JDateChooser dateChooser; // New date chooser component
    private JSpinner timeSpinner; // New time spinner component
    private JTextArea reasonArea; // Field for reason input
    private User user; // Currently logged-in user

    // Constructor
    public RendezVousPage(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.user = user;
        initComponents(); // Initialize UI components
        populateDoctorList(); // Populate the doctor list
        loadRendezVous(); // Load existing appointments
    }

    // Method to initialize UI components
    private void initComponents() {
        setTitle("Gestion des Rendez-vous");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        // Initialize components
        doctorCombo = new JComboBox<>();
        tableModel = new DefaultTableModel(new String[]{"Date", "Heure", "Médecin", "Motif"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        JTable appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(appointmentTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Vos Rendez-vous"));

        // Form panel for booking new rendez-vous
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Nouveau Rendez-vous"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date chooser
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Date :"), gbc);
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setMinSelectableDate(new Date());
        dateChooser.setPreferredSize(new Dimension(150, dateChooser.getPreferredSize().height));
        gbc.gridx = 1; formPanel.add(dateChooser, gbc);

        // Time spinner
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Heure (08:00-17:59) :"), gbc);
        SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setPreferredSize(new Dimension(80, timeSpinner.getPreferredSize().height));
        gbc.gridx = 1; formPanel.add(timeSpinner, gbc);

        // Doctor selection
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Médecin :"), gbc);
        gbc.gridx = 1; formPanel.add(doctorCombo, gbc);

        // Reason
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Motif :"), gbc);
        reasonArea = new JTextArea(3, 20);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(reasonScroll, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookBtn = new JButton("Enregistrer");
        bookBtn.addActionListener(e -> bookRendezVous());
        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(bookBtn);
        btnPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(btnPanel, gbc);

        // Main panel layout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);
    }

    // Method to populate the doctor list
    private void populateDoctorList() {
        doctorCombo.removeAllItems(); // Clear existing items
        List<String> doctors = new RendezVousDAO().getAllDoctorNames();
        if (doctors.isEmpty()) {
            doctorCombo.addItem("No doctors available");
            doctorCombo.setEnabled(false);
        } else {
            doctorCombo.setEnabled(true);
            for (String d : doctors) {
                doctorCombo.addItem(d);
            }
        }
    }

    // Method to load existing rendez-vous
    private void loadRendezVous() {
        tableModel.setRowCount(0);
        if (user == null || user.getUserId() == null) {
            System.err.println("Impossible de charger les rendez-vous : utilisateur ou ID utilisateur manquant.");
            return;
        }
        List<RendezVous> list = new RendezVousDAO().getRendezVousByUser(user.getUserId());
        SimpleDateFormat dbDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("HH:mm");

        for (RendezVous r : list) {
            try {
                Date dateTime = dbDateTimeFormat.parse(r.getDateTime());
                tableModel.addRow(new Object[]{
                    dateOnlyFormat.format(dateTime),
                    timeOnlyFormat.format(dateTime),
                    r.getDoctorName(), 
                    r.getReason()
                });
            } catch (ParseException e) {
                System.err.println("Erreur lors de l'analyse de la date/heure depuis la base de données : " + r.getDateTime() + " - " + e.getMessage());
                tableModel.addRow(new Object[]{r.getDateTime().split(" ")[0], "", r.getDoctorName(), r.getReason()});
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement du rendez-vous : " + r.getId() + " - " + e.getMessage());
            }
        }
    }

    // Method to book a new rendez-vous with validation
    private void bookRendezVous() {
        Date selectedDate = dateChooser.getDate();
        Date selectedTime = (Date) timeSpinner.getValue();
        String doctor = (String) doctorCombo.getSelectedItem();
        String reason = reasonArea.getText().trim(); // Get the reason from the text area

        // Basic validation
        if (selectedDate == null || doctor == null || doctor.equals("No doctors available") || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une date, un médecin et saisir un motif.", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Date and Time Validation ---
        Calendar combinedCal = Calendar.getInstance();
        combinedCal.setTime(selectedDate);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(selectedTime);

        combinedCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        combinedCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        combinedCal.set(Calendar.SECOND, 0);
        combinedCal.set(Calendar.MILLISECOND, 0);

        // 1. Check if date is in the past
        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0); 
        todayCal.set(Calendar.MINUTE, 0); 
        todayCal.set(Calendar.SECOND, 0); 
        todayCal.set(Calendar.MILLISECOND, 0);
        if (combinedCal.getTime().before(todayCal.getTime())) {
            JOptionPane.showMessageDialog(this, 
                "Impossible de prendre un rendez-vous pour une date passée.", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Check if time is within working hours (8:00 to 17:59)
        int hour = combinedCal.get(Calendar.HOUR_OF_DAY);
        if (hour < 8 || hour >= 18) {
            JOptionPane.showMessageDialog(this, 
                "Les rendez-vous ne peuvent être pris qu'entre 08:00 et 17:59.", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTimeString = dbFormat.format(combinedCal.getTime());

        if (user == null || user.getUserId() == null) {
            JOptionPane.showMessageDialog(this, 
                "Impossible de prendre un rendez-vous : informations utilisateur manquantes.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = new RendezVousDAO().scheduleRendezVous(user.getUserId(), doctor, dateTimeString, reason);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Rendez-vous enregistré avec succès !", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            loadRendezVous();
            dateChooser.setDate(null);
            timeSpinner.setValue(new Date()); 
            reasonArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Échec de l'enregistrement du rendez-vous. Consultez la console pour plus de détails.", 
                "Erreur d'enregistrement", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method for testing (requires jcalendar jar in classpath)
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Look and Feel Nimbus non trouvé, utilisation du style par défaut.");
        }
        
        SwingUtilities.invokeLater(() -> {
            User dummyUser = new User();
            dummyUser.setUserId("2");
            new RendezVousPage(dummyUser).setVisible(true);
        });
    }
}

