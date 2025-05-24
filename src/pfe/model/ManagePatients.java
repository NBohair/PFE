package pfe.model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import pfe.DB.PatientDAO;
import pfe.service.Patient;

public class ManagePatients extends JPanel {

    private JTable patientsTable;
    private DefaultTableModel tableModel;

    public ManagePatients() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Header =====
        JLabel headerLabel = new JLabel("Manage Patients", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(0, 102, 204));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // ===== Table =====
        String[] columnNames = {"ID", "Name", "Contact"};
        Object[][] data = {}; // Initially empty, will be populated from the database
        tableModel = new DefaultTableModel(data, columnNames);
        patientsTable = new JTable(tableModel);
        patientsTable.setFillsViewportHeight(true);
        patientsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Buttons Panel =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createButton("Add Patient", new Color(0, 153, 0));
        JButton editButton = createButton("Edit Patient", new Color(255, 165, 0));
        JButton deleteButton = createButton("Delete Patient", new Color(204, 0, 0));

        // Add action listeners
        addButton.addActionListener(e -> addPatient());
        editButton.addActionListener(e -> editPatient());
        deleteButton.addActionListener(e -> deletePatient());

        // Add buttons to the panel
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private void addPatient() {
        String name = JOptionPane.showInputDialog(this, "Enter patient's name:");
        String contact = JOptionPane.showInputDialog(this, "Enter patient's contact:");
        if (name != null && contact != null) {
            // Call DAO to add patient to the database
            new PatientDAO().addPatient(name, contact);
            JOptionPane.showMessageDialog(this, "Patient added successfully.");
            refreshPatientTable(); // Refresh the table to show the new patient
        }
    }

    private void editPatient() {
        int selectedRow = patientsTable.getSelectedRow();
        if (selectedRow != -1) {
            String newName = JOptionPane.showInputDialog(this, "Enter new name:", tableModel.getValueAt(selectedRow, 1));
            String newContact = JOptionPane.showInputDialog(this, "Enter new contact:", tableModel.getValueAt(selectedRow, 2));
            if (newName != null && newContact != null) {
                // Call DAO to update patient in the database
                new PatientDAO().updatePatient((String) tableModel.getValueAt(selectedRow, 0), newName, newContact);
                JOptionPane.showMessageDialog(this, "Patient updated successfully.");
                refreshPatientTable(); // Refresh the table to show updated information
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to edit.");
        }
    }

    private void deletePatient() {
        int selectedRow = patientsTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this patient?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Call DAO to delete patient from the database
                new PatientDAO().deletePatient((String) tableModel.getValueAt(selectedRow, 0));
                JOptionPane.showMessageDialog(this, "Patient deleted successfully.");
                refreshPatientTable(); // Refresh the table to remove the deleted patient
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete.");
        }
    }

    private void refreshPatientTable() {
        // Clear the existing data in the table model
        tableModel.setRowCount(0);
        // Fetch updated patient data from the database and populate the table
        for (Patient patient : new PatientDAO().getAllPatients()) {
            tableModel.addRow(new Object[]{patient.getId(), patient.getName(), patient.getContact()});
        }
    }
}
