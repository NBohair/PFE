package pfe.model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiagnosticTemplatesPanel extends JPanel {
    private JTable templatesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;

    public DiagnosticTemplatesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Modèles de Diagnostic");

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(StyleConstants.PANEL_BORDER);

        // Search field
        searchField = new JTextField(20);
        StyleConstants.styleTextField(searchField);
        searchField.setPreferredSize(new Dimension(250, 30));

        // Category filter
        String[] categories = {"Toutes les catégories", "Consultation générale", "Urgence", "Suivi", "Spécialité"};
        categoryCombo = new JComboBox<>(categories);
        StyleConstants.styleComboBox(categoryCombo);

        // Search button
        JButton searchButton = new JButton("Rechercher");
        StyleConstants.styleButton(searchButton);
        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(new JLabel("Rechercher:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Catégorie:"));
        searchPanel.add(categoryCombo);
        searchPanel.add(searchButton);

        // Templates Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(StyleConstants.PANEL_BORDER);

        // Create table
        String[] columns = {"Nom du diagnostic", "Catégorie", "Créé par", "Dernière modification", "Utilisations", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        templatesTable = new JTable(tableModel);
        StyleConstants.styleTable(templatesTable);

        // Add sample data
        addSampleData();

        // Adjust column widths
        templatesTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        templatesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        templatesTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        templatesTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        templatesTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        templatesTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        // Add button column
        templatesTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        templatesTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());

        tablePanel.add(new JScrollPane(templatesTable), BorderLayout.CENTER);

        // Action Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton importButton = new JButton("Importer");
        JButton exportButton = new JButton("Exporter");
        JButton newTemplateButton = new JButton("Nouveau modèle");

        StyleConstants.styleSecondaryButton(importButton);
        StyleConstants.styleSecondaryButton(exportButton);
        StyleConstants.styleButton(newTemplateButton);

        importButton.addActionListener(e -> importTemplate());
        exportButton.addActionListener(e -> exportTemplates());
        newTemplateButton.addActionListener(e -> createNewTemplate());

        buttonsPanel.add(importButton);
        buttonsPanel.add(exportButton);
        buttonsPanel.add(newTemplateButton);

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
        String query = "SELECT " +
                      "dt.id, " +
                      "dt.name AS template_name, " +
                      "dt.category, " +
                      "d.name AS doctor_name, " +
                      "dt.created_date, " +
                      "dt.last_modified, " +
                      "dt.usage_count, " +
                      "dt.description " +
                      "FROM diagnostic_templates dt " +
                      "LEFT JOIN doctors d ON dt.created_by = d.id " +
                      "ORDER BY dt.id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getString("template_name"),
                    rs.getString("category"),
                    rs.getString("doctor_name"),
                    rs.getDate("created_date").toString(),
                    rs.getString("usage_count"),
                    rs.getString("description")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération des données : " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        String category = (String) categoryCombo.getSelectedItem();
        
        // TODO: Implement actual search functionality
        JOptionPane.showMessageDialog(this,
            "Recherche: " + searchTerm + "\nCatégorie: " + category,
            "Recherche",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void importTemplate() {
        // TODO: Implement import functionality
        JOptionPane.showMessageDialog(this,
            "Importation de modèles de diagnostic",
            "Importation",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportTemplates() {
        // TODO: Implement export functionality
        JOptionPane.showMessageDialog(this,
            "Exportation des modèles de diagnostic",
            "Exportation",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void createNewTemplate() {
        // TODO: Implement template creation
        JOptionPane.showMessageDialog(this,
            "Création d'un nouveau modèle de diagnostic",
            "Nouveau modèle",
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
            setText("Éditer");
            return this;
        }
    }

    // Button editor for the actions column
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            StyleConstants.styleSecondaryButton(button);
            button.addActionListener(e -> {
                fireEditingStopped();
                editTemplate(templatesTable.getSelectedRow());
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            button.setText("Éditer");
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Éditer";
        }

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }
    }

    private void editTemplate(int row) {
        if (row >= 0) {
            String name = (String) templatesTable.getValueAt(row, 0);
            String category = (String) templatesTable.getValueAt(row, 1);
            
            String message = String.format("Édition du modèle\n\n" +
                "Nom: %s\n" +
                "Catégorie: %s\n\n" +
                "Cette fenêtre permettra de:\n" +
                "- Modifier le contenu du modèle\n" +
                "- Ajouter des sections\n" +
                "- Configurer les champs\n" +
                "- Définir les valeurs par défaut\n" +
                "- Gérer les autorisations", name, category);
            
            JOptionPane.showMessageDialog(this,
                message,
                "Édition du modèle",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 