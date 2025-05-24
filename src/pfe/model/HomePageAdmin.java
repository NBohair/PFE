package pfe.model;

import javax.swing.*;
import java.awt.*;

public class HomePageAdmin extends JFrame {
    public HomePageAdmin() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Gestion des rendez-vous des hôpitaux");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrer la fenêtre
        getContentPane().setBackground(new Color(240, 240, 240));

        // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0, 102, 204));
        menuBar.setForeground(Color.WHITE);

        // Menus principaux
        JMenu doctorsMenu = new JMenu("Doctors");
        JMenu patientsMenu = new JMenu("Patients");
        JMenu diagnosticsMenu = new JMenu("Diagnostics");
        JMenu calendarsMenu = new JMenu("Calendars");
        JMenu rdvMenu = new JMenu("Rendez-vous");
        JMenu folderMenu = new JMenu("Folder");

        // Sous-menus
        JMenuItem manageDoctorsItem = new JMenuItem("Manage Doctors");
        JMenuItem managePatientsItem = new JMenuItem("Manage Patients");

        // Action : ouvrir la fenêtre de gestion des docteurs
        manageDoctorsItem.addActionListener(e -> {
            JFrame doctorFrame = new JFrame("Gestion des patients");
            doctorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            doctorFrame.setSize(800, 600);
            doctorFrame.setLocationRelativeTo(null);
            doctorFrame.setContentPane(new ManageDoctors()); // set JPanel as content
            doctorFrame.setVisible(true);
        
        });

        // Action : ouvrir la fenêtre de gestion des patients
        managePatientsItem.addActionListener(e -> {
            JFrame patientFrame = new JFrame("Gestion des patients");
            patientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            patientFrame.setSize(800, 600);
            patientFrame.setLocationRelativeTo(null);
            patientFrame.setContentPane(new ManagePatients()); // set JPanel as content
            patientFrame.setVisible(true);
        });


        // Ajouter les items aux menus
        doctorsMenu.add(manageDoctorsItem);
        patientsMenu.add(managePatientsItem);

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
        JLabel welcomeLabel = new JLabel("Bienvenue dans le système d'administration", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePageAdmin());
    }
}
