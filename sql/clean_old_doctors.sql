USE medecin_db;

-- Suppression des anciens médecins
DELETE FROM doctors 
WHERE name IN (
    'Dr. Martin',
    'Dr. Bernard',
    'Dr. Dubois',
    'Dr. Laurent',
    'Dr. Petit'
);

-- Suppression des modèles de diagnostic associés
DELETE FROM diagnostic_templates 
WHERE created_by IN (
    SELECT id FROM doctors 
    WHERE name IN (
        'Dr. Martin',
        'Dr. Bernard',
        'Dr. Dubois',
        'Dr. Laurent',
        'Dr. Petit'
    )
);

-- Mise à jour des modèles de diagnostic existants avec les nouveaux médecins
INSERT INTO diagnostic_templates (name, category, created_by, created_date, last_modified, usage_count) 
VALUES
('Consultation générale', 'Consultation générale', 
 (SELECT id FROM doctors WHERE name = 'Dr. Mohammed El Amrani' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 125),
('Suivi diabète', 'Suivi',
 (SELECT id FROM doctors WHERE name = 'Dr. Fatima Bennis' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 45),
('Urgence cardiaque', 'Urgence',
 (SELECT id FROM doctors WHERE name = 'Dr. Ahmed Tazi' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 12),
('Bilan annuel', 'Consultation générale',
 (SELECT id FROM doctors WHERE name = 'Dr. Samira El Fassi' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 89),
('Consultation pédiatrique', 'Spécialité',
 (SELECT id FROM doctors WHERE name = 'Dr. Karim Benjelloun' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 67); 