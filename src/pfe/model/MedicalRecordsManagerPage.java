package pfe.model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pfe.service.*;
import pfe.DB.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MedicalRecordsManagerPage extends JFrame {
    private JPanel mainPanel;
    private JTable recordsTable;
    private DefaultTableModel tableModel;
    private JTextField patientIdField;
    private JTextField dateField;
    private JTextArea notesArea;
    private User user;

    public MedicalRecordsManagerPage(User user) {
        if (user == null) throw new IllegalArgumentException("User  cannot be null");
        this.user = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Manage Medical Records");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table for existing records
        tableModel = new DefaultTableModel(new String[]{"Record ID", "Patient ID", "Date", "Notes"}, 0);
        recordsTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(recordsTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Medical Records"));
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        // Form to add/edit medical record
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Record"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Patient ID field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Patient ID:"), gbc);
        patientIdField = new JTextField(10);
        gbc.gridx = 1; formPanel.add(patientIdField, gbc);

        // Date field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        dateField = new JTextField(10);
        gbc.gridx = 1; formPanel.add(dateField, gbc);

        // Notes area
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("Notes:"), gbc);
        notesArea = new JTextArea(5, 20);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        gbc.gridy = 3; formPanel.add(notesScroll, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> saveRecord());
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteRecord());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(addBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(closeBtn);
        gbc.gridy = 4; gbc.gridwidth = 2; formPanel.add(btnPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.EAST);

        add(mainPanel);
        loadRecords();
        setVisible(true);
    }

    private void loadRecords() {
        tableModel.setRowCount(0);
        List<MedicalRecord> list = MedicalRecord.getAllRecords(); // Ensure this method is correct
        for (MedicalRecord r : list) {
            tableModel.addRow(new Object[]{r.getId(), r.getPatientId(), r.getDate(), r.getNotes()});
        }
    }

    private void saveRecord() {
        String pid = patientIdField.getText().trim();
        String date = dateField.getText().trim();
        String notes = notesArea.getText().trim();
        if (pid.isEmpty() || date.isEmpty() || notes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean success = MedicalRecord.createOrUpdateRecord(pid, date, notes); // Ensure this method is correct
        if (success) {
            JOptionPane.showMessageDialog(this, "Record saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRecords();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save record.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        int selected = recordsTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selected, 0);
        boolean success = MedicalRecord.deleteRecord(id); // Ensure this method is correct
        if (success) {
            JOptionPane.showMessageDialog(this, "Record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRecords();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete record.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User dummy = new User(); // Ensure this is correctly initialized
            dummy.setUserId("1"); // Set a test user ID for demonstration
            dummy.setUsername("Test User"); // Set a test username for demonstration
            new MedicalRecordsManagerPage(dummy);
        });
    }
}



//package pfe.model;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import pfe.service.MedicalRecord;
//import pfe.service.MedicalRecordDAO;
//import pfe.service.User;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.List;
//
//public class MedicalRecordsManagerPage extends JFrame {
//    private JPanel mainPanel;
//    private JTable recordsTable;
//    private DefaultTableModel tableModel;
//    private JTextField patientIdField;
//    private JTextField dateField;
//    private JTextArea notesArea;
//    private User user;
//
//    public MedicalRecordsManagerPage(User user) {
//        if (user == null) throw new IllegalArgumentException("User cannot be null");
//        this.user = user;
//        initComponents();
//    }
//    private void initComponents() {
//        setTitle("Manage Medical Records");
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setSize(900, 600);
//        setLocationRelativeTo(null);
//        getContentPane().setBackground(new Color(240, 240, 240));
//
//        mainPanel = new JPanel(new BorderLayout(20, 20));
//        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        // Table for existing records
//        tableModel = new DefaultTableModel(new String[]{"Record ID", "Patient ID", "Date", "Notes"}, 0);
//        recordsTable = new JTable(tableModel);
//        JScrollPane tableScroll = new JScrollPane(recordsTable);
//        tableScroll.setBorder(BorderFactory.createTitledBorder("Medical Records"));
//        mainPanel.add(tableScroll, BorderLayout.CENTER);
//
//        // Form to add/edit medical record
//        JPanel formPanel = new JPanel();
//        formPanel.setLayout(new GridBagLayout());
//        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Record"));
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.anchor = GridBagConstraints.WEST;
//
//        // Patient ID field
//        gbc.gridx = 0; gbc.gridy = 0;
//        formPanel.add(new JLabel("Patient ID:"), gbc);
//        patientIdField = new JTextField(10);
//        gbc.gridx = 1; formPanel.add(patientIdField, gbc);
//
//        // Date field
//        gbc.gridx = 0; gbc.gridy = 1;
//        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
//        dateField = new JTextField(10);
//        gbc.gridx = 1; formPanel.add(dateField, gbc);
//
//        // Notes area
//        gbc.gridx = 0; gbc.gridy = 2;
//        gbc.gridwidth = 2;
//        formPanel.add(new JLabel("Notes:"), gbc);
//        notesArea = new JTextArea(5, 20);
//        JScrollPane notesScroll = new JScrollPane(notesArea);
//        gbc.gridy = 3; formPanel.add(notesScroll, gbc);
//
//        // Buttons
//        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        JButton addBtn = new JButton("Add");
//        addBtn.addActionListener(e -> saveRecord());
//        JButton deleteBtn = new JButton("Delete");
//        deleteBtn.addActionListener(e -> deleteRecord());
//        JButton closeBtn = new JButton("Close");
//        closeBtn.addActionListener(e -> dispose());
//        btnPanel.add(addBtn);
//        btnPanel.add(deleteBtn);
//        btnPanel.add(closeBtn);
//        gbc.gridy = 4; gbc.gridwidth = 2; formPanel.add(btnPanel, gbc);
//
//        mainPanel.add(formPanel, BorderLayout.EAST);
//
//        add(mainPanel);
//        loadRecords();
//        setVisible(true);
//    }}
//private void loadRecords() {
//    tableModel.setRowCount(0);
//    List<MedicalRecord> list = MedicalRecordDAO.getAllRecords();
//    for (MedicalRecord r : list) {
//        tableModel.addRow(new Object[]{r.getId(), r.getPatientId(), r.getDate(), r.getNotes()});
//    }
//}
//
//private void saveRecord() {
//    String pid = patientIdField.getText().trim();
//    String date = dateField.getText().trim();
//    String notes = notesArea.getText().trim();
//    if (pid.isEmpty() || date.isEmpty() || notes.isEmpty()) {
//        JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
//        return;
//    }
//    boolean success = MedicalRecordDAO.createOrUpdateRecord(pid, date, notes);
//    if (success) {
//        JOptionPane.showMessageDialog(this, "Record saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
//        loadRecords();
//    } else {
//        JOptionPane.showMessageDialog(this, "Failed to save record.", "Error", JOptionPane.ERROR_MESSAGE);
//    }
//}
//
//private void deleteRecord() {
//    int selected = recordsTable.getSelectedRow();
//    if (selected == -1) {
//        JOptionPane.showMessageDialog(this, "Select a record to delete.", "Error", JOptionPane.ERROR_MESSAGE);
//        return;
//    }
//    int id = (int) tableModel.getValueAt(selected, 0);
//    boolean success = MedicalRecordDAO.deleteRecord(id);
//    if (success) {
//        JOptionPane.showMessageDialog(this, "Record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
//        loadRecords();
//    } else {
//        JOptionPane.showMessageDialog(this, "Failed to delete record.", "Error", JOptionPane.ERROR_MESSAGE);
//    }
//    
//}
//
//public static void main(String[] args) {
//    SwingUtilities.invokeLater(() -> {
//        User dummy = new User();
//        dummy.setId(1);
//        dummy.setUsername("Test User");
//        new MedicalRecordsManagerPage(dummy);
//    });
//}
//}
