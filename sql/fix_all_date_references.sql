USE medecin_db;

-- Mise à jour de la vue diagnostics
DROP VIEW IF EXISTS diagnostics;
CREATE VIEW diagnostics AS 
SELECT 
    mr.id,
    mr.patient_id,
    mr.doctor_id,
    mr.date_time,
    mr.symptoms,
    mr.description,
    mr.treatment
FROM medical_records mr;

-- Mise à jour de la vue patient_history
DROP VIEW IF EXISTS patient_history;
CREATE VIEW patient_history AS
SELECT 
    p.id AS patient_id,
    p.name AS patient_name,
    d.name AS doctor_name,
    mr.date_time,
    mr.symptoms,
    mr.description,
    mr.treatment
FROM patients p
LEFT JOIN medical_records mr ON p.id = mr.patient_id
LEFT JOIN doctors d ON mr.doctor_id = d.id
ORDER BY p.name, mr.date_time DESC;

-- Mise à jour de la vue v_medical_folders
DROP VIEW IF EXISTS v_medical_folders;
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

-- Vérification et mise à jour de la table medical_history si nécessaire
ALTER TABLE medical_history
CHANGE COLUMN `date` `date_time` DATETIME NOT NULL; 