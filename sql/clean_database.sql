-- Sélectionner la base de données
USE medecin_db;

-- Étape 1: Sauvegarder les données des tables existantes dans des tables temporaires
CREATE TABLE temp_appointments AS 
SELECT 
    id,
    patient_id,
    doctor_id,
    date_time,
    COALESCE(type, 'Consultation') as type,
    reason as description,
    COALESCE(status, 'scheduled') as status,
    created_at,
    COALESCE(updated_at, created_at) as updated_at
FROM rendezvous
UNION ALL
SELECT 
    id,
    patient_id,
    doctor_id,
    date_time,
    COALESCE(type, 'Consultation') as type,
    reason as description,
    COALESCE(status, 'scheduled') as status,
    created_at,
    COALESCE(updated_at, created_at) as updated_at
FROM rendez_vous;

CREATE TABLE temp_medical_records AS 
SELECT 
    id,
    patient_id,
    doctor_id,
    COALESCE(date_consultation, created_at) as date_time,
    diagnostic as diagnosis,
    traitement as treatment,
    notes,
    COALESCE(files, '') as attachments,
    created_at,
    COALESCE(updated_at, created_at) as updated_at
FROM medicalrecords
UNION ALL
SELECT 
    id,
    patient_id,
    doctor_id,
    COALESCE(date_consultation, created_at) as date_time,
    diagnostic as diagnosis,
    traitement as treatment,
    notes,
    COALESCE(files, '') as attachments,
    created_at,
    COALESCE(updated_at, created_at) as updated_at
FROM medical_history;

-- Étape 2: Supprimer les anciennes tables
DROP TABLE IF EXISTS rendezvous;
DROP TABLE IF EXISTS rendez_vous;
DROP TABLE IF EXISTS medicalrecords;
DROP TABLE IF EXISTS medical_history;

-- Étape 3: Créer les nouvelles tables unifiées avec la bonne structure
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'Consultation',
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE medical_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    notes TEXT,
    attachments VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Étape 4: Migrer les données des tables temporaires vers les nouvelles tables
INSERT INTO appointments (
    id,
    patient_id, 
    doctor_id, 
    date_time, 
    type, 
    description, 
    status,
    created_at,
    updated_at
)
SELECT 
    id,
    patient_id,
    doctor_id,
    date_time,
    type,
    description,
    status,
    created_at,
    updated_at
FROM temp_appointments;

INSERT INTO medical_records (
    id,
    patient_id,
    doctor_id,
    date_time,
    diagnosis,
    treatment,
    notes,
    attachments,
    created_at,
    updated_at
)
SELECT 
    id,
    patient_id,
    doctor_id,
    date_time,
    diagnosis,
    treatment,
    notes,
    attachments,
    created_at,
    updated_at
FROM temp_medical_records;

-- Étape 5: Supprimer les tables temporaires
DROP TABLE temp_appointments;
DROP TABLE temp_medical_records;

-- Étape 6: Créer les index pour améliorer les performances
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_datetime ON appointments(date_time);
CREATE INDEX idx_medical_records_patient ON medical_records(patient_id);
CREATE INDEX idx_medical_records_doctor ON medical_records(doctor_id);
CREATE INDEX idx_medical_records_datetime ON medical_records(date_time);

-- Étape 7: Mettre à jour les séquences d'auto-incrémentation si nécessaire
SELECT @max_appointment_id := COALESCE(MAX(id), 0) FROM appointments;
SELECT @max_medical_record_id := COALESCE(MAX(id), 0) FROM medical_records;

ALTER TABLE appointments AUTO_INCREMENT = @max_appointment_id + 1;
ALTER TABLE medical_records AUTO_INCREMENT = @max_medical_record_id + 1; 