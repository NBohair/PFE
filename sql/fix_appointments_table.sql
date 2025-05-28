USE medecin_db;

-- Ajout de la colonne type à la table appointments
ALTER TABLE appointments
ADD COLUMN type ENUM('Consultation', 'Suivi', 'Urgence', 'Contrôle') 
NOT NULL DEFAULT 'Consultation' 
AFTER date_time;

-- Mise à jour de la vue rendezvous pour inclure le type
DROP VIEW IF EXISTS rendezvous;
CREATE VIEW rendezvous AS 
SELECT id, patient_id, doctor_id, date_time, type, reason, status 
FROM appointments; 