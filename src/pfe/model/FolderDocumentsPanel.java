package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.File;

public class FolderDocumentsPanel extends JPanel {
    private int folderId;
    private Connection conn;
    private JTable documentsTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat;

    public FolderDocumentsPanel(int folderId) {
        this.folderId = folderId;
        this.conn = DatabaseConnection.getConnection();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadDocuments();
    }

    private void initComponents() {
        // Table
        String[] columns = {"Date", "Type", "Titre", "Description", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        documentsTable = new JTable(tableModel);
        documentsTable.setRowHeight(35);
        documentsTable.setShowGrid(true);
        documentsTable.setGridColor(new Color(230, 230, 230));
        documentsTable.getTableHeader().setReorderingAllowed(false);
        documentsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        documentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < documentsTable.getColumnCount() - 1; i++) {
            documentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Action column
        documentsTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        documentsTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(documentsTable);
        scrollPane.setBorder(StyleConstants.PANEL_BORDER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Ajouter un document");
        StyleConstants.styleButton(addButton);
        addButton.addActionListener(e -> addDocument());
        buttonsPanel.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadDocuments() {
        tableModel.setRowCount(0);
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM medical_documents WHERE folder_id = ? ORDER BY created_at DESC");
            stmt.setInt(1, folderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    dateFormat.format(rs.getTimestamp("created_at")),
                    rs.getString("document_type"),
                    rs.getString("title"),
                    rs.getString("description"),
                    "Voir"
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des documents: " + e.getMessage(),
                "Erreur de base de données",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDocument() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Ajouter un document", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Type
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(new JLabel("Type:"), gbc);

        String[] types = {"prescription", "analyse", "radiologie", "autre"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        StyleConstants.styleComboBox(typeCombo);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        // Title
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Titre:"), gbc);

        JTextField titleField = new JTextField(30);
        StyleConstants.styleTextField(titleField);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        // Description
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Description:"), gbc);

        JTextArea descArea = new JTextArea(4, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descArea);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);

        // File
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Fichier:"), gbc);

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField filePathField = new JTextField(20);
        filePathField.setEditable(false);
        JButton browseButton = new JButton("Parcourir");
        StyleConstants.styleSecondaryButton(browseButton);
        
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        filePanel.add(filePathField);
        filePanel.add(browseButton);
        gbc.gridx = 1;
        panel.add(filePanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        StyleConstants.styleButton(saveButton);
        StyleConstants.styleSecondaryButton(cancelButton);

        saveButton.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Veuillez saisir un titre",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO medical_documents (folder_id, document_type, title, description, file_path) " +
                    "VALUES (?, ?, ?, ?, ?)");
                
                stmt.setInt(1, folderId);
                stmt.setString(2, (String) typeCombo.getSelectedItem());
                stmt.setString(3, titleField.getText().trim());
                stmt.setString(4, descArea.getText().trim());
                stmt.setString(5, filePathField.getText());

                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(dialog,
                    "Document ajouté avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadDocuments();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'ajout du document: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

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

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            StyleConstants.styleSecondaryButton(button);
            button.addActionListener(e -> {
                fireEditingStopped();
                int row = documentsTable.getSelectedRow();
                if (row >= 0) {
                    viewDocument(row);
                }
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

    private void viewDocument(int row) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM medical_documents WHERE folder_id = ? LIMIT 1 OFFSET ?");
            stmt.setInt(1, folderId);
            stmt.setInt(2, row);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String filePath = rs.getString("file_path");
                if (filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Le fichier n'existe pas: " + filePath,
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Aucun fichier associé à ce document",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture du document: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 