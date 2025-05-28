-- Création de la base de données avec support UTF-8
DROP DATABASE IF EXISTS medecin_db;
CREATE DATABASE medecin_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE medecin_db;

-- Table des spécialités
CREATE TABLE specialties (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB;

-- Table des médecins
CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100),
    contact VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    address TEXT
) ENGINE=InnoDB;

-- Table des patients
CREATE TABLE patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    birth_date DATE,
    contact VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    address TEXT
) ENGINE=InnoDB;

-- Table des rendez-vous
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    reason TEXT,
    status VARCHAR(20) DEFAULT 'scheduled',
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
) ENGINE=InnoDB;

-- Table des diagnostics
CREATE TABLE medical_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    date DATE NOT NULL,
    symptoms TEXT,
    description TEXT,
    treatment TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
) ENGINE=InnoDB;

-- Vue pour la compatibilité avec l'ancien nom 'diagnostics'
CREATE VIEW diagnostics AS 
SELECT * FROM medical_records;

-- Table des dossiers médicaux
CREATE TABLE medical_folders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    status VARCHAR(50),
    last_visit_date DATE,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(id)
) ENGINE=InnoDB;

-- Table de l'historique médical
CREATE TABLE medical_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    folder_id INT NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    FOREIGN KEY (folder_id) REFERENCES medical_folders(id)
) ENGINE=InnoDB;

-- Vue pour les dossiers médicaux
CREATE VIEW v_medical_folders AS
SELECT 
    mf.id,
    p.name AS patient_name,
    mf.last_visit_date,
    mf.status,
    COUNT(DISTINCT mr.id) AS document_count,
    COUNT(DISTINCT mh.id) AS history_count,
    COUNT(DISTINCT mr2.id) AS treatment_count
FROM medical_folders mf
JOIN patients p ON mf.patient_id = p.id
LEFT JOIN medical_records mr ON p.id = mr.patient_id
LEFT JOIN medical_history mh ON mf.id = mh.folder_id
LEFT JOIN medical_records mr2 ON p.id = mr2.patient_id
GROUP BY mf.id, p.name, mf.last_visit_date, mf.status;

-- Vue pour la compatibilité avec l'ancien nom 'rendezvous'
CREATE VIEW rendezvous AS 
SELECT * FROM appointments;

-- Index pour optimiser les recherches
CREATE INDEX idx_doctor_specialty ON doctors(specialty);
CREATE INDEX idx_patient_name ON patients(name);
CREATE INDEX idx_appointment_datetime ON appointments(date_time);
CREATE INDEX idx_medical_record_date ON medical_records(date);
CREATE INDEX idx_medical_folder_patient ON medical_folders(patient_id);

-- Insertion des spécialités de base
INSERT INTO specialties (name, description) VALUES
('Médecine générale', 'Médecine de premier recours'),
('Cardiologie', 'Spécialité du cœur et des vaisseaux'),
('Pédiatrie', 'Médecine des enfants'),
('Dermatologie', 'Spécialité de la peau'),
('Ophtalmologie', 'Spécialité des yeux'); 