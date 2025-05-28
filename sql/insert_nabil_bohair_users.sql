USE medecin_db;

-- Création de la table users si elle n'existe pas
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'user') DEFAULT 'user',
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Suppression des données existantes pour éviter les doublons
DELETE FROM appointments WHERE patient_id IN (SELECT id FROM patients WHERE name LIKE 'Nabil Bohair%');
DELETE FROM medical_records WHERE patient_id IN (SELECT id FROM patients WHERE name LIKE 'Nabil Bohair%');
DELETE FROM patients WHERE name LIKE 'Nabil Bohair%';
DELETE FROM doctors WHERE name LIKE 'Dr. Nabil Bohair%';
DELETE FROM users WHERE username LIKE 'nabil.bohair%';

-- Insertion des utilisateurs avec le même mot de passe (1234)
INSERT INTO users (username, password, role, first_name, last_name, email) VALUES
('nabil.bohair1', '1234', 'admin', 'Nabil', 'Bohair', 'nabil.bohair1@admin.ma'),
('nabil.bohair2', '1234', 'admin', 'Nabil', 'Bohair', 'nabil.bohair2@admin.ma'),
('nabil.bohair2.doc', '1234', 'user', 'Nabil', 'Bohair', 'nabil.bohair2@doctor.ma'),
('nabil.bohair3', '1234', 'user', 'Nabil', 'Bohair', 'nabil.bohair3@doctor.ma'),
('nabil.bohair4', '1234', 'user', 'Nabil', 'Bohair', 'nabil.bohair4@patient.ma');

-- Insertion des médecins
INSERT INTO doctors (name, specialty, contact, email, address) VALUES
('Dr. Nabil Bohair 2', 'Médecine générale', '0600000001', 'nabil.bohair2@doctor.ma', 'Cabinet Médical - Casablanca'),
('Dr. Nabil Bohair 3', 'Cardiologie', '0600000002', 'nabil.bohair3@doctor.ma', 'Cabinet Médical - Casablanca');

-- Insertion du patient
INSERT INTO patients (name, birth_date, contact, email, address) VALUES
('Nabil Bohair 4', '1990-01-01', '0600000003', 'nabil.bohair4@patient.ma', 'Rue Hassan II, N°10, Casablanca'); 