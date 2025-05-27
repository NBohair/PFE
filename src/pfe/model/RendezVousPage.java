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
        setTitle("Manage Rendez-Vous");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 600); // Adjusted size slightly
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        // Initialize components
        doctorCombo = new JComboBox<>();
        tableModel = new DefaultTableModel(new String[]{"Date", "Time", "Doctor", "Reason"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               // Make table cells non-editable
               return false;
            }
        };
        JTable appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow single row selection
        JScrollPane tableScroll = new JScrollPane(appointmentTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Your Rendez-Vous"));

        // Form panel for booking new rendez-vous
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book New Rendez-Vous"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow components to expand horizontally

        // Date chooser - Restricted to today and future dates
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Date:"), gbc);
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setMinSelectableDate(new Date()); // Set minimum date to today
        dateChooser.setPreferredSize(new Dimension(150, dateChooser.getPreferredSize().height)); // Set preferred width
        gbc.gridx = 1; formPanel.add(dateChooser, gbc);

        // Time spinner - Initialized to current time
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Time (08:00-17:59):"), gbc); // Updated label
        // Configure SpinnerDateModel for time selection
        SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        timeSpinner = new JSpinner(timeModel);
        // Set editor to display time only (HH:mm format)
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setPreferredSize(new Dimension(80, timeSpinner.getPreferredSize().height)); // Set preferred width
        gbc.gridx = 1; formPanel.add(timeSpinner, gbc);

        // Doctor selection
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 1; formPanel.add(doctorCombo, gbc);

        // Reason
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Reason:"), gbc);
        reasonArea = new JTextArea(3, 20);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1; // Reset gridwidth if needed
        formPanel.add(reasonScroll, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookBtn = new JButton("Book");
        bookBtn.addActionListener(e -> bookRendezVous());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose()); // Close only this window
        btnPanel.add(bookBtn);
        btnPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.EAST; // Align button panel to the right
        formPanel.add(btnPanel, gbc);

        // Main panel layout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.EAST);

        // Add main panel to frame
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
            System.err.println("Cannot load appointments: User or User ID is null.");
            return;
        }
        List<RendezVous> list = new RendezVousDAO().getRendezVousByUser(user.getUserId());
        SimpleDateFormat dbDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Format expected from DB
        SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("HH:mm");

        for (RendezVous r : list) {
            try {
                // Parse the date/time string from the RendezVous object using DB format
                Date dateTime = dbDateTimeFormat.parse(r.getDateTime());
                tableModel.addRow(new Object[]{
                    dateOnlyFormat.format(dateTime), // Display date part
                    timeOnlyFormat.format(dateTime), // Display time part
                    r.getDoctorName(), 
                    r.getReason() // Display the reason
                });
            } catch (ParseException e) {
                 System.err.println("Error parsing date/time from DB: " + r.getDateTime() + " - " + e.getMessage());
                 // Attempt to display raw date if parsing fails
                 tableModel.addRow(new Object[]{r.getDateTime().split(" ")[0], "", r.getDoctorName(), r.getReason()});
            } catch (Exception e) {
                System.err.println("Error processing appointment: " + r.getId() + " - " + e.getMessage());
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
            JOptionPane.showMessageDialog(this, "Please select a date, doctor, and enter a reason.", "Input Error", JOptionPane.ERROR_MESSAGE);
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

        // 1. Check if date is in the past (redundant due to JDateChooser setting, but good practice)
        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0); todayCal.set(Calendar.MINUTE, 0); todayCal.set(Calendar.SECOND, 0); todayCal.set(Calendar.MILLISECOND, 0);
        if (combinedCal.getTime().before(todayCal.getTime())) {
             JOptionPane.showMessageDialog(this, "Cannot book appointments for past dates.", "Input Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        // 2. Check if time is within working hours (8:00 to 17:59)
        int hour = combinedCal.get(Calendar.HOUR_OF_DAY);
        if (hour < 8 || hour >= 18) { // Hours are 0-23
            JOptionPane.showMessageDialog(this, "Appointments can only be booked between 08:00 and 17:59.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // --- End Validation ---

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTimeString = dbFormat.format(combinedCal.getTime());

        if (user == null || user.getUserId() == null) {
             JOptionPane.showMessageDialog(this, "Cannot book appointment: User information is missing.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        // Proceed with booking - *** CORRECTED CALL ***
        boolean success = new RendezVousDAO().scheduleRendezVous(user.getUserId(), doctor, dateTimeString, reason);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Rendez-Vous booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRendezVous(); // Reload the appointments
            // Clear form
            dateChooser.setDate(null);
            timeSpinner.setValue(new Date()); 
            reasonArea.setText("");
        } else {
            // The DAO method now prints detailed errors, so this message can be more general
            JOptionPane.showMessageDialog(this, "Failed to book Rendez-Vous. Check console for details.", "Booking Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method for testing (requires jcalendar jar in classpath)
    public static void main(String[] args) {
         // Set Nimbus Look and Feel for better appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus Look and Feel not found, using default.");
        }
        
        SwingUtilities.invokeLater(() -> {
            User dummyUser = new User(); // Replace with actual user retrieval if needed
            dummyUser.setUserId("2"); // Use an ID that likely exists as a patient (e.g., linked to user 'user')
            // Ensure the dummy user ID exists as a patient in the database for testing getRendezVousByUser
            new RendezVousPage(dummyUser).setVisible(true);
        });
    }
}

