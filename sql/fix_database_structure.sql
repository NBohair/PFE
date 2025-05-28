USE medecin_db;

-- Suppression des vues existantes pour éviter les conflits
DROP VIEW IF EXISTS user_roles;
DROP VIEW IF EXISTS v_medical_folders;

-- Correction des noms de tables si nécessaire
SET @renameTable = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'medecin_db' 
    AND table_name = 'rendezvous'
);
SET @sql = IF(@renameTable > 0, 'RENAME TABLE rendezvous TO appointments', 'SELECT "Table rendezvous does not exist"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @renameDiagnostics = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'medecin_db' 
    AND table_name = 'diagnostics'
);
SET @sql = IF(@renameDiagnostics > 0, 'RENAME TABLE diagnostics TO medical_records', 'SELECT "Table diagnostics does not exist"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Création de la vue medical_folders
CREATE OR REPLACE VIEW v_medical_folders AS
SELECT 
    mr.id,
    p.name AS patient_name,
    d.name AS doctor_name,
    mr.date_time,
    mr.diagnosis,
    mr.treatment,
    mr.prescription,
    mr.notes
FROM medical_records mr
JOIN patients p ON mr.patient_id = p.id
JOIN doctors d ON mr.doctor_id = d.id;

-- Recréation de la vue user_roles
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

-- Vérification et correction de la table doctors si la colonne specialty_id n'existe pas
SET @exist_specialty_id := (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'medecin_db' 
    AND TABLE_NAME = 'doctors' 
    AND COLUMN_NAME = 'specialty_id'
);

SET @sql := IF(@exist_specialty_id = 0,
    'ALTER TABLE doctors ADD COLUMN specialty_id INT,
     ADD FOREIGN KEY (specialty_id) REFERENCES specialties(id)',
    'SELECT "Column specialty_id already exists"'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Mise à jour des index avec vérification d'existence
SET @checkIndex = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'medecin_db'
    AND table_name = 'doctors'
    AND index_name = 'idx_doctor_specialty'
);
SET @sql = IF(@checkIndex = 0, 'ALTER TABLE doctors ADD INDEX idx_doctor_specialty (specialty_id)', 'SELECT "Index idx_doctor_specialty already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Vérification et ajout des index pour appointments
SET @checkIndex = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'medecin_db'
    AND table_name = 'appointments'
    AND index_name = 'idx_appointment_datetime'
);
SET @sql = IF(@checkIndex = 0, 'ALTER TABLE appointments ADD INDEX idx_appointment_datetime (date_time)', 'SELECT "Index idx_appointment_datetime already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @checkIndex = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'medecin_db'
    AND table_name = 'appointments'
    AND index_name = 'idx_appointment_patient'
);
SET @sql = IF(@checkIndex = 0, 'ALTER TABLE appointments ADD INDEX idx_appointment_patient (patient_id)', 'SELECT "Index idx_appointment_patient already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @checkIndex = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'medecin_db'
    AND table_name = 'appointments'
    AND index_name = 'idx_appointment_doctor'
);
SET @sql = IF(@checkIndex = 0, 'ALTER TABLE appointments ADD INDEX idx_appointment_doctor (doctor_id)', 'SELECT "Index idx_appointment_doctor already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Vérification et ajout des index pour medical_records
SET @checkIndex = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'medecin_db'
    AND table_name = 'medical_records'
    AND index_name = 'idx_record_patient'
);
SET @sql = IF(@checkIndex = 0, 'ALTER TABLE medical_records ADD INDEX idx_record_patient (patient_id)', 'SELECT "Index idx_record_patient already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @checkIndex = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'medecin_db'
    AND table_name = 'medical_records'
    AND index_name = 'idx_record_doctor'
);
SET @sql = IF(@checkIndex = 0, 'ALTER TABLE medical_records ADD INDEX idx_record_doctor (doctor_id)', 'SELECT "Index idx_record_doctor already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @checkIndex = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'medecin_db'
    AND table_name = 'medical_records'
    AND index_name = 'idx_record_datetime'
);
SET @sql = IF(@checkIndex = 0, 'ALTER TABLE medical_records ADD INDEX idx_record_datetime (date_time)', 'SELECT "Index idx_record_datetime already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Vérification des contraintes de clés étrangères
SET foreign_key_checks = 0;

-- Vérification et ajout des contraintes pour appointments
SET @checkConstraint = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = 'medecin_db'
    AND table_name = 'appointments'
    AND constraint_name = 'fk_appointment_patient'
);
SET @sql = IF(@checkConstraint = 0, 'ALTER TABLE appointments ADD CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients(id)', 'SELECT "Constraint fk_appointment_patient already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @checkConstraint = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = 'medecin_db'
    AND table_name = 'appointments'
    AND constraint_name = 'fk_appointment_doctor'
);
SET @sql = IF(@checkConstraint = 0, 'ALTER TABLE appointments ADD CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)', 'SELECT "Constraint fk_appointment_doctor already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Vérification et ajout des contraintes pour medical_records
SET @checkConstraint = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = 'medecin_db'
    AND table_name = 'medical_records'
    AND constraint_name = 'fk_record_patient'
);
SET @sql = IF(@checkConstraint = 0, 'ALTER TABLE medical_records ADD CONSTRAINT fk_record_patient FOREIGN KEY (patient_id) REFERENCES patients(id)', 'SELECT "Constraint fk_record_patient already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @checkConstraint = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = 'medecin_db'
    AND table_name = 'medical_records'
    AND constraint_name = 'fk_record_doctor'
);
SET @sql = IF(@checkConstraint = 0, 'ALTER TABLE medical_records ADD CONSTRAINT fk_record_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)', 'SELECT "Constraint fk_record_doctor already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET foreign_key_checks = 1;

-- Mise à jour des colonnes manquantes dans les tables existantes
ALTER TABLE medical_records
MODIFY COLUMN diagnosis TEXT,
MODIFY COLUMN treatment TEXT,
MODIFY COLUMN prescription TEXT,
MODIFY COLUMN notes TEXT; 