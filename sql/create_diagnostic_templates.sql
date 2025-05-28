USE medecin_db;

-- Création de la table diagnostic_templates
CREATE TABLE IF NOT EXISTS diagnostic_templates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    created_by INT NOT NULL,
    created_date DATE NOT NULL,
    last_modified DATE NOT NULL,
    usage_count INT DEFAULT 0,
    description TEXT,
    FOREIGN KEY (created_by) REFERENCES doctors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Suppression des anciens médecins
DELETE FROM doctors 
WHERE name IN (
    'Dr. Martin',
    'Dr. Bernard',
    'Dr. Dubois',
    'Dr. Laurent',
    'Dr. Petit'
);

-- Insertion des modèles de diagnostic avec les nouveaux médecins
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