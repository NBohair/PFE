package pfe.model;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarViewPanel extends JPanel {
    private String viewType;
    private Calendar currentDate;
    private JPanel calendarGrid;
    private JLabel monthLabel;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");

    public CalendarViewPanel(String viewType) {
        this.viewType = viewType;
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        currentDate = Calendar.getInstance();
        initComponents();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        StyleConstants.styleHeaderPanel(headerPanel, "Calendrier - Vue " + viewType);

        // Navigation Panel
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navigationPanel.setBackground(Color.WHITE);
        navigationPanel.setBorder(StyleConstants.PANEL_BORDER);

        JButton prevButton = new JButton("◀");
        monthLabel = new JLabel(getDateLabelText());
        monthLabel.setFont(StyleConstants.TITLE_FONT);
        JButton nextButton = new JButton("▶");
        JButton todayButton = new JButton("Aujourd'hui");

        StyleConstants.styleSecondaryButton(prevButton);
        StyleConstants.styleSecondaryButton(nextButton);
        StyleConstants.styleButton(todayButton);

        prevButton.addActionListener(e -> navigateCalendar(-1));
        nextButton.addActionListener(e -> navigateCalendar(1));
        todayButton.addActionListener(e -> goToToday());

        navigationPanel.add(prevButton);
        navigationPanel.add(monthLabel);
        navigationPanel.add(nextButton);
        navigationPanel.add(Box.createHorizontalStrut(20));
        navigationPanel.add(todayButton);

        // Calendar Grid
        calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarGrid.setBackground(Color.WHITE);
        calendarGrid.setBorder(StyleConstants.PANEL_BORDER);

        // Legend Panel
        JPanel legendPanel = createLegendPanel();

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(StyleConstants.BACKGROUND_COLOR);
        contentPanel.add(navigationPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(calendarGrid), BorderLayout.CENTER);
        contentPanel.add(legendPanel, BorderLayout.SOUTH);

        // Add all panels
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Initial update
        updateCalendarGrid();
    }

    private String getDateLabelText() {
        SimpleDateFormat sdf;
        switch (viewType.toLowerCase()) {
            case "journalière":
                sdf = new SimpleDateFormat("EEEE d MMMM yyyy");
                break;
            case "hebdomadaire":
                sdf = new SimpleDateFormat("'Semaine' w, MMMM yyyy");
                break;
            default: // mensuelle
                sdf = monthFormat;
                break;
        }
        return sdf.format(currentDate.getTime());
    }

    private void updateCalendarGrid() {
        calendarGrid.removeAll();
        
        // Add day headers
        String[] days = {"Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(StyleConstants.SUBTITLE_FONT);
            calendarGrid.add(label);
        }

        Calendar cal = (Calendar) currentDate.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        // Fill in the calendar
        int firstDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Add empty cells before first day
        for (int i = 0; i < firstDay; i++) {
            calendarGrid.add(new JLabel());
        }
        
        // Add days
        for (int i = 1; i <= maxDays; i++) {
            JPanel dayPanel = createDayPanel(i);
            calendarGrid.add(dayPanel);
        }
        
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private JPanel createDayPanel(int day) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel dateLabel = new JLabel(String.valueOf(day), SwingConstants.RIGHT);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        Calendar today = Calendar.getInstance();
        if (currentDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            currentDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            day == today.get(Calendar.DAY_OF_MONTH)) {
            dateLabel.setForeground(new Color(0, 102, 204));
            dateLabel.setFont(StyleConstants.SUBTITLE_FONT);
        }
        
        panel.add(dateLabel, BorderLayout.NORTH);
        
        // Add sample appointments (replace with actual data)
        if (Math.random() < 0.3) { // 30% chance of having an appointment
            JLabel appointmentLabel = new JLabel("RDV: Dr. El Amrani", SwingConstants.CENTER);
            appointmentLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            appointmentLabel.setForeground(new Color(46, 204, 113));
            panel.add(appointmentLabel, BorderLayout.CENTER);
        }
        
        return panel;
    }

    private void navigateCalendar(int delta) {
        switch (viewType.toLowerCase()) {
            case "journalière":
                currentDate.add(Calendar.DAY_OF_MONTH, delta);
                break;
            case "hebdomadaire":
                currentDate.add(Calendar.WEEK_OF_YEAR, delta);
                break;
            default: // mensuelle
                currentDate.add(Calendar.MONTH, delta);
                break;
        }
        updateCalendarGrid();
        monthLabel.setText(getDateLabelText());
    }

    private void goToToday() {
        currentDate = Calendar.getInstance();
        updateCalendarGrid();
        monthLabel.setText(getDateLabelText());
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Add legend items
        addLegendItem(legendPanel, new Color(46, 204, 113), "Disponible");
        addLegendItem(legendPanel, new Color(231, 76, 60), "Occupé");
        addLegendItem(legendPanel, new Color(0, 102, 204), "Aujourd'hui");

        return legendPanel;
    }

    private void addLegendItem(JPanel panel, Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(Color.WHITE);

        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        dot.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel label = new JLabel(text);
        label.setFont(StyleConstants.REGULAR_FONT);

        item.add(dot);
        item.add(label);
        panel.add(item);
    }
} 