package pfe.model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientFolderPanel extends JPanel {
    private JTable folderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;

    public PatientFolderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Dossiers Médicaux");

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(StyleConstants.PANEL_BORDER);

        // Search field
        searchField = new JTextField(20);
        StyleConstants.styleTextField(searchField);
        searchField.setPreferredSize(new Dimension(250, 30));
        
        // Filter combo
        String[] filters = {"Tous les dossiers", "Consultations récentes", "Dossiers archivés", "Urgences"};
        filterCombo = new JComboBox<>(filters);
        StyleConstants.styleComboBox(filterCombo);

        // Search button
        JButton searchButton = new JButton("Rechercher");
        StyleConstants.styleButton(searchButton);
        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(new JLabel("Rechercher:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filtrer par:"));
        searchPanel.add(filterCombo);
        searchPanel.add(searchButton);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(StyleConstants.PANEL_BORDER);

        // Create table
        String[] columns = {"ID", "Patient", "Dernière visite", "Médecin traitant", "Statut", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column is editable
            }
        };
        folderTable = new JTable(tableModel);
        StyleConstants.styleTable(folderTable);

        // Add sample data
        addSampleData();

        // Add button column
        folderTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        folderTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());

        // Adjust column widths
        folderTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        folderTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        folderTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        folderTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        folderTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        folderTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        tablePanel.add(new JScrollPane(folderTable), BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton newFolderButton = new JButton("Nouveau dossier");
        JButton printButton = new JButton("Imprimer");
        JButton exportButton = new JButton("Exporter");

        StyleConstants.styleButton(newFolderButton);
        StyleConstants.styleSecondaryButton(printButton);
        StyleConstants.styleSecondaryButton(exportButton);

        newFolderButton.addActionListener(e -> createNewFolder());
        printButton.addActionListener(e -> printFolders());
        exportButton.addActionListener(e -> exportFolders());

        buttonsPanel.add(exportButton);
        buttonsPanel.add(printButton);
        buttonsPanel.add(newFolderButton);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add all panels
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addSampleData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Object[][] data = {
            {"001", "Youssef Alami", "15/03/2024", "Dr. Mohammed El Amrani", "Actif", ""},
            {"002", "Fatima Zidane", "12/03/2024", "Dr. Fatima Bennis", "Actif", ""},
            {"003", "Karim Idrissi", "10/03/2024", "Dr. Ahmed Tazi", "Archivé", ""},
            {"004", "Amina Benjelloun", "08/03/2024", "Dr. Samira El Fassi", "Actif", ""},
            {"005", "Hassan El Fassi", "05/03/2024", "Dr. Karim Benjelloun", "Urgent", ""}
        };

        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        String filter = (String) filterCombo.getSelectedItem();
        
        // TODO: Implement actual search functionality
        JOptionPane.showMessageDialog(this,
            "Recherche: " + searchTerm + "\nFiltre: " + filter,
            "Recherche",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void createNewFolder() {
        // TODO: Implement new folder creation
        JOptionPane.showMessageDialog(this,
            "Création d'un nouveau dossier médical",
            "Nouveau dossier",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void printFolders() {
        // TODO: Implement print functionality
        JOptionPane.showMessageDialog(this,
            "Impression des dossiers sélectionnés",
            "Impression",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportFolders() {
        // TODO: Implement export functionality
        JOptionPane.showMessageDialog(this,
            "Exportation des dossiers sélectionnés",
            "Exportation",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Button renderer for the actions column
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            StyleConstants.styleSecondaryButton(this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Voir");
            return this;
        }
    }

    // Button editor for the actions column
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor() {
            super(new JTextField());
            button = new JButton();
            StyleConstants.styleSecondaryButton(button);
            button.addActionListener(e -> {
                fireEditingStopped();
                showFolderDetails(folderTable.getSelectedRow());
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            button.setText("Voir");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Voir";
        }
    }

    private void showFolderDetails(int row) {
        if (row >= 0) {
            String patientName = (String) folderTable.getValueAt(row, 1);
            String message = String.format("Détails du dossier de %s\n\n" +
                "Cette fonctionnalité permettra de voir:\n" +
                "- Historique médical\n" +
                "- Prescriptions\n" +
                "- Analyses\n" +
                "- Documents joints\n" +
                "- Notes des médecins", patientName);
            
            JOptionPane.showMessageDialog(this,
                message,
                "Détails du dossier",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 