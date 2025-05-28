package pfe.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/medecin_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection = null;
    private static final Properties properties = new Properties();

    static {
        // Configuration des propriétés de connexion
        properties.setProperty("user", USER);
        properties.setProperty("password", PASSWORD);
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("useSSL", "false");
        properties.setProperty("serverTimezone", "UTC");
        properties.setProperty("characterEncoding", "UTF-8");
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à la base de données réussie!");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver MySQL introuvable: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void testConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Test de connexion réussi!");
            try {
                System.out.println("Version de la base de données: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("URL de connexion: " + conn.getMetaData().getURL());
                System.out.println("Nom d'utilisateur: " + conn.getMetaData().getUserName());
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération des métadonnées: " + e.getMessage());
            }
        } else {
            System.err.println("Échec du test de connexion!");
        }
    }

    public static void main(String[] args) {
        // Test de la connexion
        testConnection();
        // Fermer la connexion après le test
        closeConnection();
    }

    public static void beginTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.setAutoCommit(false);
        }
    }

    public static void commitTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    public static void rollbackTransaction() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            showError("Error rolling back transaction", e);
        }
    }

    private static void showError(String message, Exception e) {
        String fullMessage = message + "\n" + e.getMessage();
        System.err.println(fullMessage);
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
            fullMessage,
            "Erreur de base de données",
            JOptionPane.ERROR_MESSAGE);
    }
} 