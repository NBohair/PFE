USE medecin_db;

-- Renommer la colonne date en date_time dans la table medical_records
ALTER TABLE medical_records
CHANGE COLUMN `date` `date_time` DATETIME NOT NULL;

-- Mise à jour de la vue diagnostics pour utiliser date_time
DROP VIEW IF EXISTS diagnostics;
CREATE VIEW diagnostics AS 
SELECT 
    id,
    patient_id,
    doctor_id,
    date_time,
    symptoms,
    description,
    treatment
FROM medical_records;

-- Mise à jour de la vue patient_history si elle existe
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