USE medcin_db;

-- Ajouter la colonne type si elle n'existe pas
ALTER TABLE rendezvous ADD COLUMN IF NOT EXISTS type VARCHAR(50) DEFAULT 'Consultation';

-- Mettre à jour les rendez-vous existants
UPDATE rendezvous SET type = 'Consultation' WHERE type IS NULL;

-- Mettre à jour les types de rendez-vous existants avec une distribution
UPDATE rendezvous 
SET type = CASE 
    WHEN id % 4 = 0 THEN 'Consultation'
    WHEN id % 4 = 1 THEN 'Suivi'
    WHEN id % 4 = 2 THEN 'Urgence'
    WHEN id % 4 = 3 THEN 'Contrôle'
    ELSE 'Consultation'
END; 