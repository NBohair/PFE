USE medecin_db;

-- Suppression des anciens modèles de diagnostic
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

-- Insertion des nouveaux modèles avec les médecins marocains
INSERT INTO diagnostic_templates (name, category, created_by, created_date, last_modified, usage_count, description) 
VALUES
('Consultation générale', 'Consultation générale', 
 (SELECT id FROM doctors WHERE name = 'Dr. Mohammed El Amrani' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 125,
 'Modèle standard pour les consultations générales'),

('Suivi diabète', 'Suivi',
 (SELECT id FROM doctors WHERE name = 'Dr. Fatima Bennis' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 45,
 'Suivi régulier des patients diabétiques'),

('Urgence cardiaque', 'Urgence',
 (SELECT id FROM doctors WHERE name = 'Dr. Ahmed Tazi' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 12,
 'Protocole pour les urgences cardiaques'),

('Bilan annuel', 'Consultation générale',
 (SELECT id FROM doctors WHERE name = 'Dr. Samira El Fassi' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 89,
 'Bilan de santé annuel complet'),

('Consultation pédiatrique', 'Spécialité',
 (SELECT id FROM doctors WHERE name = 'Dr. Karim Benjelloun' LIMIT 1),
 CURRENT_DATE, CURRENT_DATE, 67,
 'Consultation spécialisée pour enfants'); 