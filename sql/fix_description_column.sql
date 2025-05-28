USE medecin_db;

-- Ajout de la colonne description à la table appointments si elle n'existe pas
ALTER TABLE appointments
MODIFY COLUMN reason TEXT DEFAULT NULL,
ADD COLUMN description TEXT DEFAULT NULL AFTER reason;

-- Mise à jour de la vue rendezvous
DROP VIEW IF EXISTS rendezvous;
CREATE VIEW rendezvous AS 
SELECT 
    id,
    patient_id,
    doctor_id,
    date_time,
    type,
    reason,
    description,
    status
FROM appointments;

-- Mise à jour de la vue diagnostics
DROP VIEW IF EXISTS diagnostics;
CREATE VIEW diagnostics AS 
SELECT 
    id,
    patient_id,
    doctor_id,
    date,
    symptoms,
    description,
    treatment
FROM medical_records; 