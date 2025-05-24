package pfe.model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import pfe.DB.*;
import pfe.service.*;

public class ManageDoctors extends JPanel {

    private JTable doctorsTable;
    private DefaultTableModel tableModel;

    public ManageDoctors() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Header =====
        JLabel headerLabel = new JLabel("Manage Doctors", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(0, 102, 204));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // ===== Table =====
        String[] columnNames = {"ID", "Name", "Specialty"};
        Object[][] data = {}; // Initially empty, will be populated from the database
        tableModel = new DefaultTableModel(data, columnNames);
        doctorsTable = new JTable(tableModel);
        doctorsTable.setFillsViewportHeight(true);
        doctorsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(doctorsTable);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Buttons Panel =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createButton("Add Doctor", new Color(0, 153, 0));
        JButton editButton = createButton("Edit Doctor", new Color(255, 165, 0));
        JButton deleteButton = createButton("Delete Doctor", new Color(204, 0, 0));

        // Add action listeners
        addButton.addActionListener(e -> addDoctor());
        editButton.addActionListener(e -> editDoctor());
        deleteButton.addActionListener(e -> deleteDoctor());

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

    private void addDoctor() {
        String name = JOptionPane.showInputDialog(this, "Enter doctor's name:");
        String specialty = JOptionPane.showInputDialog(this, "Enter doctor's specialty:");
        if (name != null && specialty != null) {
            // Call DAO to add doctor to the database
            new MedecinDAO().addMedecin(name, specialty);
            JOptionPane.showMessageDialog(this, "Doctor added successfully.");
            refreshDoctorTable(); // Refresh the table to show the new doctor
        }
    }

    private void editDoctor() {
        int selectedRow = doctorsTable.getSelectedRow();
        if (selectedRow != -1) {
            String newName = JOptionPane.showInputDialog(this, "Enter new name:", tableModel.getValueAt(selectedRow, 1));
            String newSpecialty = JOptionPane.showInputDialog(this, "Enter new specialty:", tableModel.getValueAt(selectedRow, 2));
            if (newName != null && newSpecialty != null) {
                // Call DAO to update doctor in the database
                // Assuming MedecinDAO has an updateMedecin method
                new MedecinDAO().updateMedecin((String) tableModel.getValueAt(selectedRow, 0), newName, newSpecialty);
                JOptionPane.showMessageDialog(this, "Doctor updated successfully.");
                refreshDoctorTable(); // Refresh the table to show updated information
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a doctor to edit.");
        }
    }

    private void deleteDoctor() {
        int selectedRow = doctorsTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this doctor?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Call DAO to delete doctor from the database
                new MedecinDAO().deleteMedecin((String) tableModel.getValueAt(selectedRow, 0));
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully.");
                refreshDoctorTable(); // Refresh the table to remove the deleted doctor
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.");
        }
    }

    private void refreshDoctorTable() {
        // Clear the existing data in the table model
        tableModel.setRowCount(0);
        // Fetch updated doctor data from the database and populate the table
        // Assuming MedecinDAO has a method to get all doctors
        for (Doctor doctor : new MedecinDAO().getAllDoctors()) {
            tableModel.addRow(new Object[]{doctor.getId(), doctor.getName(), doctor.getSpecialty()});
        }
    }
}
