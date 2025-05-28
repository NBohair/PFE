package pfe.model;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class DoctorStatsPanel extends JPanel {
    private JComboBox<String> doctorCombo;
    private JComboBox<String> periodCombo;

    public DoctorStatsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Statistiques des Médecins");
        add(headerPanel, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(StyleConstants.PANEL_BORDER);

        // Doctor ComboBox
        JComboBox<String> doctorCombo = new JComboBox<>();
        doctorCombo.addItem("Tous les médecins");
        doctorCombo.addItem("Dr. Ahmed El Amrani - Cardiology");
        doctorCombo.addItem("Dr. Fatima Zahra Benali - Dermatology");
        StyleConstants.styleComboBox(doctorCombo);

        // Period ComboBox
        JComboBox<String> periodCombo = new JComboBox<>(new String[]{
            "Cette semaine",
            "Ce mois",
            "Ce trimestre",
            "Cette année"
        });
        StyleConstants.styleComboBox(periodCombo);

        // Update Button
        JButton updateButton = new JButton("Actualiser");
        StyleConstants.styleButton(updateButton);

        filterPanel.add(new JLabel("Médecin:"));
        filterPanel.add(doctorCombo);
        filterPanel.add(new JLabel("Période:"));
        filterPanel.add(periodCombo);
        filterPanel.add(updateButton);

        // Stats Grid Panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Add stat cards
        statsPanel.add(createStatCard("Patients", "157", "+12%", true));
        statsPanel.add(createStatCard("Temps moyen/patient", "23 min", "-5%", false));
        statsPanel.add(createStatCard("Taux d'occupation", "87%", "+3%", true));
        statsPanel.add(createStatCard("Annulations", "8", "-2%", true));

        // Charts Panel
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        chartsPanel.add(createChartPanel("Rendez-vous par jour", true));
        chartsPanel.add(createChartPanel("Types de consultations", false));

        // Layout
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(statsPanel, BorderLayout.CENTER);
        contentPanel.add(chartsPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Add action listener to update button
        updateButton.addActionListener(e -> updateStats(
            doctorCombo.getSelectedItem().toString(),
            periodCombo.getSelectedItem().toString()
        ));
    }

    private JPanel createStatCard(String title, String value, String change, boolean isPositive) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(StyleConstants.PANEL_BORDER);

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));

        // Change
        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(StyleConstants.SMALL_FONT);
        changeLabel.setForeground(isPositive ? new Color(46, 204, 113) : new Color(231, 76, 60));
        changeLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(changeLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createChartPanel(String title, boolean isLineChart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(StyleConstants.PANEL_BORDER);

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(StyleConstants.SUBTITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Chart (simulated with random data)
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int padding = 30;

                if (isLineChart) {
                    drawLineChart(g2d, width, height, padding);
                } else {
                    drawPieChart(g2d, width, height);
                }
            }
        };
        chartPanel.setPreferredSize(new Dimension(400, 200));
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private void drawLineChart(Graphics2D g2d, int width, int height, int padding) {
        int[] data = new int[7];
        Random rand = new Random();
        for (int i = 0; i < data.length; i++) {
            data[i] = rand.nextInt(height - 2 * padding) + padding;
        }

        // Draw axes
        g2d.setColor(Color.GRAY);
        g2d.drawLine(padding, height - padding, width - padding, height - padding);
        g2d.drawLine(padding, padding, padding, height - padding);

        // Draw line chart
        g2d.setColor(StyleConstants.PRIMARY_COLOR);
        g2d.setStroke(new BasicStroke(2f));
        for (int i = 0; i < data.length - 1; i++) {
            int x1 = padding + i * ((width - 2 * padding) / (data.length - 1));
            int x2 = padding + (i + 1) * ((width - 2 * padding) / (data.length - 1));
            int y1 = data[i];
            int y2 = data[i + 1];
            g2d.drawLine(x1, y1, x2, y2);
            g2d.fillOval(x1 - 4, y1 - 4, 8, 8);
        }
        g2d.fillOval(width - padding - 4, data[data.length - 1] - 4, 8, 8);
    }

    private void drawPieChart(Graphics2D g2d, int width, int height) {
        int size = Math.min(width, height) - 40;
        int x = (width - size) / 2;
        int y = (height - size) / 2;

        // Sample data
        int[] values = {35, 25, 20, 15, 5};
        Color[] colors = {
            new Color(46, 204, 113),
            new Color(52, 152, 219),
            new Color(155, 89, 182),
            new Color(241, 196, 15),
            new Color(231, 76, 60)
        };

        int startAngle = 0;
        for (int i = 0; i < values.length; i++) {
            g2d.setColor(colors[i]);
            int arcAngle = (int) (values[i] / 100.0 * 360);
            g2d.fillArc(x, y, size, size, startAngle, arcAngle);
            startAngle += arcAngle;
        }
    }

    private void updateStats(String doctor, String period) {
        // TODO: Implement actual database queries to update statistics
        // For now, we'll just show a message
        JOptionPane.showMessageDialog(this,
            "Mise à jour des statistiques pour:\nMédecin: " + doctor + "\nPériode: " + period,
            "Mise à jour",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 