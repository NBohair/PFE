USE medecin_db;

-- Vérification de l'existence de la colonne specialty_id
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'medecin_db'
    AND table_name = 'doctors'
    AND column_name = 'specialty_id'
);

-- Si specialty_id n'existe pas, on le crée
SET @add_column = IF(@column_exists = 0,
    'ALTER TABLE doctors 
     ADD COLUMN specialty_id INT,
     ADD FOREIGN KEY (specialty_id) REFERENCES specialties(id)',
    'SELECT "La colonne specialty_id existe déjà."'
);

PREPARE stmt FROM @add_column;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Migration des données si nécessaire
SET @has_specialty = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'medecin_db'
    AND table_name = 'doctors'
    AND column_name = 'specialty'
);

-- Si les deux colonnes existent, on fait la migration
SET @migrate_data = IF(@has_specialty > 0 AND @column_exists = 0,
    'UPDATE doctors d
     INNER JOIN specialties s ON d.specialty = s.name
     SET d.specialty_id = s.id',
    'SELECT "Pas besoin de migration des données."'
);

PREPARE stmt FROM @migrate_data;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Suppression des vues existantes
DROP VIEW IF EXISTS diagnostics;
DROP VIEW IF EXISTS patient_history;
DROP VIEW IF EXISTS v_medical_folders;
DROP VIEW IF EXISTS rendezvous;

-- Recréation de la vue diagnostics
CREATE VIEW diagnostics AS 
SELECT 
    mr.id,
    mr.patient_id,
    mr.doctor_id,
    mr.date_time,
    mr.symptoms,
    mr.diagnosis,
    mr.treatment
FROM medical_records mr;

-- Recréation de la vue patient_history
CREATE VIEW patient_history AS
SELECT 
    p.id AS patient_id,
    p.name AS patient_name,
    d.name AS doctor_name,
    s.name AS specialty,
    mr.date_time,
    mr.symptoms,
    mr.diagnosis,
    mr.treatment
FROM patients p
LEFT JOIN medical_records mr ON p.id = mr.patient_id
LEFT JOIN doctors d ON mr.doctor_id = d.id
LEFT JOIN specialties s ON d.specialty_id = s.id
ORDER BY p.name, mr.date_time DESC;

-- Recréation de la vue v_medical_folders
CREATE VIEW v_medical_folders AS
SELECT 
    mf.id,
    p.name AS patient_name,
    mf.last_visit_date,
    mf.status,
    COUNT(DISTINCT mr.id) AS document_count,
    COUNT(DISTINCT mh.id) AS history_count,
    COUNT(DISTINCT mr2.id) AS treatment_count,
    MAX(mr.diagnosis) AS latest_diagnosis
FROM medical_folders mf
JOIN patients p ON mf.patient_id = p.id
LEFT JOIN medical_records mr ON p.id = mr.patient_id
LEFT JOIN medical_history mh ON mf.id = mh.folder_id
LEFT JOIN medical_records mr2 ON p.id = mr2.patient_id
GROUP BY mf.id, p.name, mf.last_visit_date, mf.status;

-- Recréation de la vue rendezvous
CREATE VIEW rendezvous AS
SELECT 
    a.id,
    a.patient_id,
    a.doctor_id,
    a.date_time,
    a.type,
    a.reason,
    a.description,
    a.status
FROM appointments a; 