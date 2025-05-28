USE medecin_db;

-- Création de la table des administrateurs
CREATE TABLE IF NOT EXISTS administrators (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('super_admin', 'admin') NOT NULL DEFAULT 'admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Ajout des administrateurs Nabil Bohair
INSERT INTO administrators (name, email, phone, password_hash, role) VALUES
('Nabil Bohair 1', 'nabil.bohair1@admin.com', '0661111111', SHA2('password123', 256), 'super_admin'),
('Nabil Bohair 2', 'nabil.bohair2@admin.com', '0662222222', SHA2('password123', 256), 'admin');

-- Ajout de Nabil Bohair 2 comme médecin également
INSERT INTO doctors (name, specialty_id, phone, email, address) VALUES
('Dr. Nabil Bohair 2', 1, '0662222222', 'nabil.bohair2@doctor.com', 'Avenue Mohammed V, N°100, Casablanca');

-- Ajout de Nabil Bohair 3 comme patient
INSERT INTO patients (name, birth_date, gender, phone, email, address, blood_type, allergies) VALUES
('Nabil Bohair 3', '1990-01-01', 'M', '0663333333', 'nabil.bohair3@gmail.com', 'Rue Al Massira, N°50, Casablanca', 'O+', NULL);

-- Création d'une vue pour voir tous les rôles des utilisateurs
CREATE OR REPLACE VIEW user_roles AS
SELECT 
    name,
    'administrator' as role_type,
    email,
    phone,
    created_at
FROM administrators
UNION
SELECT 
    name,
    'doctor' as role_type,
    email,
    phone,
    created_at
FROM doctors
UNION
SELECT 
    name,
    'patient' as role_type,
    email,
    phone,
    created_at
FROM patients;

-- Ajout d'un rendez-vous pour Nabil Bohair 3 avec Dr. Nabil Bohair 2
INSERT INTO appointments (patient_id, doctor_id, date_time, type, reason, status) VALUES
((SELECT id FROM patients WHERE email = 'nabil.bohair3@gmail.com'),
 (SELECT id FROM doctors WHERE email = 'nabil.bohair2@doctor.com'),
 NOW() + INTERVAL 10 DAY,
 'Consultation',
 'Consultation générale',
 'scheduled'); 