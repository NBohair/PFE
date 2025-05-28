package pfe.model;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import java.util.Vector;
import com.toedter.calendar.JDateChooser;

public class DiagnosticPanel extends JPanel {
    private JTextArea diagnosticArea;
    private JTextArea symptomsArea;
    private JTextArea treatmentArea;
    private JComboBox<ComboItem> patientCombo;
    private JComboBox<ComboItem> doctorCombo;
    private JDateChooser dateChooser;
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

    public DiagnosticPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        conn = DatabaseConnection.getConnection();
        initComponents();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Nouveau Diagnostic");
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(StyleConstants.REGULAR_FONT);

        // Information Panel
        JPanel infoPanel = createInfoPanel();
        tabbedPane.addTab("Informations", infoPanel);

        // Diagnostic Panel
        JPanel diagnosticPanel = createDiagnosticPanel();
        tabbedPane.addTab("Diagnostic", diagnosticPanel);

        // Treatment Panel
        JPanel treatmentPanel = createTreatmentPanel();
        tabbedPane.addTab("Traitement", treatmentPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton saveButton = new JButton("Enregistrer");
        JButton clearButton = new JButton("Effacer");

        StyleConstants.styleButton(saveButton);
        StyleConstants.styleSecondaryButton(clearButton);

        saveButton.addActionListener(e -> saveDiagnostic());
        clearButton.addActionListener(e -> clearForm());

        buttonsPanel.add(clearButton);
        buttonsPanel.add(saveButton);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Patient selection
        addFormField(panel, "Patient:", createPatientComboBox(), gbc, 0);
        
        // Doctor selection
        addFormField(panel, "Médecin:", createDoctorComboBox(), gbc, 1);
        
        // Date field
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date());
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.getDateEditor().setEnabled(false);
        addFormField(panel, "Date:", dateChooser, gbc, 2);

        return panel;
    }

    private JPanel createDiagnosticPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Symptoms section
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel symptomsLabel = new JLabel("Symptômes:");
        symptomsLabel.setFont(StyleConstants.SUBTITLE_FONT);
        panel.add(symptomsLabel, gbc);

        gbc.gridy = 1;
        symptomsArea = new JTextArea(5, 40);
        StyleConstants.styleTextArea(symptomsArea);
        panel.add(new JScrollPane(symptomsArea), gbc);

        // Diagnostic section
        gbc.gridy = 2;
        JLabel diagnosticLabel = new JLabel("Diagnostic:");
        diagnosticLabel.setFont(StyleConstants.SUBTITLE_FONT);
        panel.add(diagnosticLabel, gbc);

        gbc.gridy = 3;
        diagnosticArea = new JTextArea(10, 40);
        StyleConstants.styleTextArea(diagnosticArea);
        panel.add(new JScrollPane(diagnosticArea), gbc);

        return panel;
    }

    private JPanel createTreatmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel treatmentLabel = new JLabel("Traitement prescrit:");
        treatmentLabel.setFont(StyleConstants.SUBTITLE_FONT);
        panel.add(treatmentLabel, gbc);

        gbc.gridy = 1;
        treatmentArea = new JTextArea(15, 40);
        StyleConstants.styleTextArea(treatmentArea);
        panel.add(new JScrollPane(treatmentArea), gbc);

        return panel;
    }

    private JComboBox<ComboItem> createPatientComboBox() {
        Vector<ComboItem> patients = new Vector<>();
        patients.add(new ComboItem(0, "Sélectionner un patient"));
        
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
        
        doctorCombo = new JComboBox<>(doctors);
        StyleConstants.styleComboBox(doctorCombo);
        return doctorCombo;
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

    private void saveDiagnostic() {
        ComboItem selectedPatient = (ComboItem) patientCombo.getSelectedItem();
        ComboItem selectedDoctor = (ComboItem) doctorCombo.getSelectedItem();
        
        // Validation des données
        if (!validateDiagnosticData(selectedPatient, selectedDoctor)) {
            return;
        }

        try {
            DatabaseConnection.beginTransaction();
            try {
                // Insert diagnostic
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO diagnostics (patient_id, doctor_id, date_time, symptoms, diagnosis, treatment) " +
                    "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                
                stmt.setInt(1, selectedPatient.getId());
                stmt.setInt(2, selectedDoctor.getId());
                stmt.setDate(3, new java.sql.Date(dateChooser.getDate().getTime()));
                stmt.setString(4, symptomsArea.getText().trim());
                stmt.setString(5, diagnosticArea.getText().trim());
                stmt.setString(6, treatmentArea.getText().trim());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("La création du diagnostic a échoué, aucune ligne affectée.");
                }

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int diagnosticId = generatedKeys.getInt(1);
                    System.out.println("Nouveau diagnostic créé avec l'ID: " + diagnosticId);
                }
                
                stmt.close();
                DatabaseConnection.commitTransaction();
                
                JOptionPane.showMessageDialog(this,
                    "Diagnostic enregistré avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                clearForm();
                
            } catch (SQLException ex) {
                DatabaseConnection.rollbackTransaction();
                throw ex;
            }
        } catch (SQLException e) {
            String errorMessage = "Erreur lors de l'enregistrement du diagnostic: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                errorMessage,
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateDiagnosticData(ComboItem selectedPatient, ComboItem selectedDoctor) {
        StringBuilder errors = new StringBuilder();

        if (selectedPatient == null || selectedPatient.getId() == 0) {
            errors.append("- Veuillez sélectionner un patient\n");
        }
        
        if (selectedDoctor == null || selectedDoctor.getId() == 0) {
            errors.append("- Veuillez sélectionner un médecin\n");
        }
        
        if (dateChooser.getDate() == null) {
            errors.append("- Veuillez sélectionner une date\n");
        }
        
        if (diagnosticArea.getText().trim().isEmpty()) {
            errors.append("- Le diagnostic est requis\n");
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez corriger les erreurs suivantes:\n" + errors.toString(),
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        diagnosticArea.setText("");
        symptomsArea.setText("");
        treatmentArea.setText("");
        patientCombo.setSelectedIndex(0);
        doctorCombo.setSelectedIndex(0);
        dateChooser.setDate(new Date());
    }
} 