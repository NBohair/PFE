package pfe.model;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StyleConstants {
    // Colors
    public static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    public static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    public static final Color TEXT_COLOR = new Color(51, 51, 51);
    public static final Color BORDER_COLOR = new Color(200, 200, 200);

    // Fonts
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);

    // Borders
    public static final Border PANEL_BORDER = BorderFactory.createLineBorder(BORDER_COLOR, 1);
    public static final int PADDING = 10;

    // Style methods
    public static void styleHeaderPanel(JPanel panel, String title) {
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(Color.WHITE);
        panel.add(headerLabel);
    }

    public static void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(REGULAR_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY_COLOR);
        button.setFont(REGULAR_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(REGULAR_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            PANEL_BORDER,
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    public static void styleTextArea(JTextArea textArea) {
        textArea.setFont(REGULAR_FONT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            PANEL_BORDER,
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(REGULAR_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(PANEL_BORDER);
        ((JComponent) comboBox.getRenderer()).setPreferredSize(new Dimension(200, 25));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(REGULAR_FONT);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(BACKGROUND_COLOR);
        table.getTableHeader().setFont(SUBTITLE_FONT);

        // Center align cells by default
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Selection colors
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(TEXT_COLOR);
    }

    public static void styleTableWithCustomWidths(JTable table, int[] columnWidths) {
        styleTable(table);
        for (int i = 0; i < Math.min(columnWidths.length, table.getColumnCount()); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    public static void styleScheduleTable(JTable table) {
        // Basic table styling
        table.setRowHeight(45);
        table.setFont(REGULAR_FONT);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(TEXT_COLOR);

        // Header styling
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(SUBTITLE_FONT);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));

        // Time column styling (first column)
        DefaultTableCellRenderer timeRenderer = new DefaultTableCellRenderer();
        timeRenderer.setHorizontalAlignment(JLabel.CENTER);
        timeRenderer.setBackground(new Color(240, 240, 240));
        timeRenderer.setFont(new Font("Arial", Font.BOLD, 12));
        table.getColumnModel().getColumn(0).setCellRenderer(timeRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);

        // Appointment cells styling
        DefaultTableCellRenderer appointmentRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null && !value.toString().isEmpty()) {
                    setBackground(new Color(230, 240, 255));
                    setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 210, 240)));
                } else {
                    setBackground(Color.WHITE);
                    setBorder(table.getBorder());
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };

        // Apply appointment renderer to all columns except the time column
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(appointmentRenderer);
            table.getColumnModel().getColumn(i).setPreferredWidth(150);
        }
    }
} 