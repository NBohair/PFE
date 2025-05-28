package pfe.model;

import javax.swing.*;
import java.awt.*;

public class HomePageAdmin extends JFrame {
    public HomePageAdmin() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Système de Gestion Hospitalière");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240));

        // Style commun pour tous les menus
        Font menuFont = new Font("Arial", Font.BOLD, 12);
        Color menuForeground = Color.WHITE;

        // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0, 102, 204));
        menuBar.setForeground(Color.WHITE);

        // Menus principaux
        JMenu doctorsMenu = createStyledMenu("Médecins", menuFont, menuForeground);
        JMenu patientsMenu = createStyledMenu("Patients", menuFont, menuForeground);
        JMenu diagnosticsMenu = createStyledMenu("Diagnostics", menuFont, menuForeground);
        JMenu calendarsMenu = createStyledMenu("Calendriers", menuFont, menuForeground);
        JMenu rdvMenu = createStyledMenu("Rendez-vous", menuFont, menuForeground);
        JMenu folderMenu = createStyledMenu("Dossiers", menuFont, menuForeground);

        // Configuration des items du menu Médecins
        JMenuItem manageDoctorsItem = createStyledMenuItem("Gérer les médecins", menuFont);
        JMenuItem doctorScheduleItem = createStyledMenuItem("Planning des médecins", menuFont);
        JMenuItem doctorStatsItem = createStyledMenuItem("Statistiques", menuFont);

        // Configuration des items du menu Patients
        JMenuItem managePatientsItem = createStyledMenuItem("Gérer les patients", menuFont);
        JMenuItem patientHistoryItem = createStyledMenuItem("Historique des patients", menuFont);
        JMenuItem patientSearchItem = createStyledMenuItem("Rechercher un patient", menuFont);

        // Configuration des items du menu Diagnostics
        JMenuItem newDiagnosticItem = createStyledMenuItem("Nouveau diagnostic", menuFont);
        JMenuItem diagnosticHistoryItem = createStyledMenuItem("Historique des diagnostics", menuFont);
        JMenuItem diagnosticTemplatesItem = createStyledMenuItem("Modèles de diagnostic", menuFont);

        // Configuration des items du menu Calendriers
        JMenuItem dayViewItem = createStyledMenuItem("Vue journalière", menuFont);
        JMenuItem weekViewItem = createStyledMenuItem("Vue hebdomadaire", menuFont);
        JMenuItem monthViewItem = createStyledMenuItem("Vue mensuelle", menuFont);

        // Configuration des items du menu Rendez-vous
        JMenuItem newAppointmentItem = createStyledMenuItem("Nouveau rendez-vous", menuFont);
        JMenuItem manageAppointmentsItem = createStyledMenuItem("Gérer les rendez-vous", menuFont);
        JMenuItem appointmentHistoryItem = createStyledMenuItem("Historique", menuFont);

        // Configuration des items du menu Dossiers
        JMenuItem manageFoldersItem = createStyledMenuItem("Dossiers médicaux", menuFont);
        folderMenu.add(manageFoldersItem);

        // Action : ouvrir la gestion des dossiers
        manageFoldersItem.addActionListener(e -> {
            JFrame folderFrame = new JFrame("Gestion des Dossiers Médicaux");
            folderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            folderFrame.setSize(1024, 768);
            folderFrame.setLocationRelativeTo(null);
            folderFrame.setContentPane(new MedicalFolderPanel());
            folderFrame.setVisible(true);
        });

        // Action : ouvrir la fenêtre de gestion des médecins
        manageDoctorsItem.addActionListener(e -> {
            JFrame doctorFrame = new JFrame("Gestion des médecins");
            doctorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            doctorFrame.setSize(800, 600);
            doctorFrame.setLocationRelativeTo(null);
            doctorFrame.setContentPane(new ManageDoctors());
            doctorFrame.setVisible(true);
        });

        // Action : ouvrir la fenêtre de planning des médecins
        doctorScheduleItem.addActionListener(e -> {
            JFrame scheduleFrame = new JFrame("Planning des médecins");
            scheduleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            scheduleFrame.setSize(800, 600);
            scheduleFrame.setLocationRelativeTo(null);
            scheduleFrame.setContentPane(new DoctorSchedulePanel());
            scheduleFrame.setVisible(true);
        });

        // Action : ouvrir la fenêtre de gestion des patients
        managePatientsItem.addActionListener(e -> {
            JFrame patientFrame = new JFrame("Gestion des patients");
            patientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            patientFrame.setSize(800, 600);
            patientFrame.setLocationRelativeTo(null);
            patientFrame.setContentPane(new ManagePatients());
            patientFrame.setVisible(true);
        });

        // Action : ouvrir la fenêtre de nouveau diagnostic
        newDiagnosticItem.addActionListener(e -> {
            JFrame diagnosticFrame = new JFrame("Nouveau diagnostic");
            diagnosticFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            diagnosticFrame.setSize(800, 600);
            diagnosticFrame.setLocationRelativeTo(null);
            diagnosticFrame.setContentPane(new DiagnosticPanel());
            diagnosticFrame.setVisible(true);
        });

        // Action : ouvrir l'historique des diagnostics
        diagnosticHistoryItem.addActionListener(e -> {
            JFrame historyFrame = new JFrame("Historique des diagnostics");
            historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            historyFrame.setSize(1024, 768);
            historyFrame.setLocationRelativeTo(null);
            historyFrame.setContentPane(new DiagnosticHistoryPanel());
            historyFrame.setVisible(true);
        });

        // Action : ouvrir les modèles de diagnostic
        diagnosticTemplatesItem.addActionListener(e -> {
            JFrame templatesFrame = new JFrame("Modèles de diagnostic");
            templatesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            templatesFrame.setSize(1024, 768);
            templatesFrame.setLocationRelativeTo(null);
            templatesFrame.setContentPane(new DiagnosticTemplatesPanel());
            templatesFrame.setVisible(true);
        });

        // Action : ouvrir le calendrier
        dayViewItem.addActionListener(e -> openCalendarView("Journalière"));
        weekViewItem.addActionListener(e -> openCalendarView("Hebdomadaire"));
        monthViewItem.addActionListener(e -> openCalendarView("Mensuelle"));

        // Actions pour les autres items (messages temporaires)
        doctorStatsItem.addActionListener(e -> {
            JFrame statsFrame = new JFrame("Statistiques des médecins");
            statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            statsFrame.setSize(1024, 768);
            statsFrame.setLocationRelativeTo(null);
            statsFrame.setContentPane(new DoctorStatsPanel());
            statsFrame.setVisible(true);
        });

        patientHistoryItem.addActionListener(e -> {
            JFrame historyFrame = new JFrame("Historique des patients");
            historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            historyFrame.setSize(1024, 768);
            historyFrame.setLocationRelativeTo(null);
            historyFrame.setContentPane(new PatientHistoryPanel());
            historyFrame.setVisible(true);
        });

        patientSearchItem.addActionListener(e -> {
            JFrame searchFrame = new JFrame("Recherche de patients");
            searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            searchFrame.setSize(1024, 768);
            searchFrame.setLocationRelativeTo(null);
            searchFrame.setContentPane(new PatientSearchPanel());
            searchFrame.setVisible(true);
        });

        manageAppointmentsItem.addActionListener(e -> {
            JFrame appointmentsFrame = new JFrame("Gestion des rendez-vous");
            appointmentsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            appointmentsFrame.setSize(1024, 768);
            appointmentsFrame.setLocationRelativeTo(null);
            appointmentsFrame.setContentPane(new ManageAppointmentsPanel());
            appointmentsFrame.setVisible(true);
        });

        // Ajouter les items aux menus
        doctorsMenu.add(manageDoctorsItem);
        doctorsMenu.add(doctorScheduleItem);
        doctorsMenu.add(doctorStatsItem);

        patientsMenu.add(managePatientsItem);
        patientsMenu.add(patientHistoryItem);
        patientsMenu.add(patientSearchItem);

        diagnosticsMenu.add(newDiagnosticItem);
        diagnosticsMenu.add(diagnosticHistoryItem);
        diagnosticsMenu.add(diagnosticTemplatesItem);

        calendarsMenu.add(dayViewItem);
        calendarsMenu.add(weekViewItem);
        calendarsMenu.add(monthViewItem);

        rdvMenu.add(newAppointmentItem);
        rdvMenu.add(manageAppointmentsItem);
        rdvMenu.add(appointmentHistoryItem);

        folderMenu.add(manageFoldersItem);

        // Ajouter tous les menus à la barre
        menuBar.add(doctorsMenu);
        menuBar.add(patientsMenu);
        menuBar.add(diagnosticsMenu);
        menuBar.add(calendarsMenu);
        menuBar.add(rdvMenu);
        menuBar.add(folderMenu);

        // Appliquer la barre de menu à la fenêtre
        setJMenuBar(menuBar);

        // Contenu de la fenêtre principale
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        JLabel welcomeLabel = new JLabel("Bienvenue dans le Système de Gestion Hospitalière", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 102, 204));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Ajouter le panneau principal
        add(mainPanel);

        setVisible(true);
    }

    private JMenu createStyledMenu(String text, Font font, Color foreground) {
        JMenu menu = new JMenu(text);
        menu.setFont(font);
        menu.setForeground(foreground);
        return menu;
    }

    private JMenuItem createStyledMenuItem(String text, Font font) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(font);
        return item;
    }

    private void showComingSoon(String feature) {
        JOptionPane.showMessageDialog(this,
            feature + " - Cette fonctionnalité sera bientôt disponible!",
            "En développement",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void openCalendarView(String viewType) {
        JFrame calendarFrame = new JFrame("Calendrier - Vue " + viewType);
        calendarFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        calendarFrame.setSize(1024, 768);
        calendarFrame.setLocationRelativeTo(null);
        calendarFrame.setContentPane(new CalendarViewPanel(viewType));
        calendarFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePageAdmin());
    }
}
