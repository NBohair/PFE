USE medecin_db;

-- Mise à jour de la table medical_records
ALTER TABLE medical_records
MODIFY COLUMN date_time DATETIME NOT NULL;

-- Mise à jour de la table medical_history
ALTER TABLE medical_history
MODIFY COLUMN date_time DATETIME NOT NULL;

-- Suppression de toutes les vues pour les recréer proprement
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
LEFT JOIN specialties s ON d.specialty = s.name
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