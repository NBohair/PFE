-- Créer la nouvelle base de données
DROP DATABASE IF EXISTS medecin_db;
CREATE DATABASE medecin_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE medecin_db;

-- Table des spécialités
CREATE TABLE specialties (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Table des médecins
CREATE TABLE doctors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialty_id INT,
    phone VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (specialty_id) REFERENCES specialties(id),
    INDEX idx_doctor_specialty (specialty_id)
) ENGINE=InnoDB;

-- Table des patients
CREATE TABLE patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    birth_date DATE,
    gender ENUM('M', 'F'),
    phone VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    address TEXT,
    blood_type VARCHAR(5),
    allergies TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_name (name)
) ENGINE=InnoDB;

-- Table des rendez-vous
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    type ENUM('Consultation', 'Suivi', 'Urgence', 'Contrôle') NOT NULL DEFAULT 'Consultation',
    reason TEXT,
    status ENUM('scheduled', 'confirmed', 'completed', 'cancelled') NOT NULL DEFAULT 'scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    INDEX idx_appointment_datetime (date_time),
    INDEX idx_appointment_patient (patient_id),
    INDEX idx_appointment_doctor (doctor_id)
) ENGINE=InnoDB;

-- Table des dossiers médicaux
CREATE TABLE medical_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT,
    notes TEXT,
    attachments VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    INDEX idx_record_patient (patient_id),
    INDEX idx_record_doctor (doctor_id),
    INDEX idx_record_datetime (date_time)
) ENGINE=InnoDB;

-- Table des médicaments prescrits
CREATE TABLE prescriptions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    medical_record_id INT NOT NULL,
    medication_name VARCHAR(100) NOT NULL,
    dosage VARCHAR(50),
    frequency VARCHAR(50),
    duration VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id),
    INDEX idx_prescription_record (medical_record_id)
) ENGINE=InnoDB;

-- Insérer quelques spécialités de base
INSERT INTO specialties (name, description) VALUES
('Médecine générale', 'Médecine de premier recours et suivi général des patients'),
('Cardiologie', 'Spécialité des maladies du cœur et des vaisseaux'),
('Pédiatrie', 'Spécialité médicale des enfants et adolescents'),
('Dermatologie', 'Spécialité des maladies de la peau'),
('Ophtalmologie', 'Spécialité des yeux et de la vision');

-- Créer une vue pour l'historique complet des patients
CREATE VIEW patient_history AS
SELECT 
    p.id AS patient_id,
    p.name AS patient_name,
    d.name AS doctor_name,
    s.name AS specialty,
    a.date_time,
    a.type AS appointment_type,
    a.reason,
    a.status,
    mr.diagnosis,
    mr.treatment,
    mr.prescription
FROM patients p
LEFT JOIN appointments a ON p.id = a.patient_id
LEFT JOIN doctors d ON a.doctor_id = d.id
LEFT JOIN specialties s ON d.specialty_id = s.id
LEFT JOIN medical_records mr ON p.id = mr.patient_id AND a.date_time = mr.date_time
ORDER BY p.name, a.date_time DESC;

-- Créer une procédure stockée pour obtenir l'historique d'un patient
DELIMITER //
CREATE PROCEDURE get_patient_history(IN patient_id_param INT)
BEGIN
    SELECT * FROM patient_history
    WHERE patient_id = patient_id_param
    ORDER BY date_time DESC;
END //
DELIMITER ;

-- Créer un trigger pour mettre à jour la date de modification
DELIMITER //
CREATE TRIGGER update_appointment_timestamp
BEFORE UPDATE ON appointments
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END //
DELIMITER ;

-- Créer un trigger pour la validation des rendez-vous
DELIMITER //
CREATE TRIGGER before_appointment_insert
BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
    DECLARE doctor_exists INT;
    DECLARE patient_exists INT;
    DECLARE conflicting_appointments INT;

    -- Vérifier si le médecin existe
    SELECT COUNT(*) INTO doctor_exists FROM doctors WHERE id = NEW.doctor_id;
    IF doctor_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Le médecin spécifié n''existe pas';
    END IF;

    -- Vérifier si le patient existe
    SELECT COUNT(*) INTO patient_exists FROM patients WHERE id = NEW.patient_id;
    IF patient_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Le patient spécifié n''existe pas';
    END IF;

    -- Vérifier les conflits de rendez-vous
    SELECT COUNT(*) INTO conflicting_appointments
    FROM appointments
    WHERE doctor_id = NEW.doctor_id
    AND date_time BETWEEN NEW.date_time - INTERVAL 30 MINUTE
    AND NEW.date_time + INTERVAL 30 MINUTE
    AND status != 'cancelled';

    IF conflicting_appointments > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Il y a déjà un rendez-vous prévu à cette heure';
    END IF;
END //
DELIMITER ; 