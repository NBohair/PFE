USE medecin_db;

-- Insertion des médecins spécifiques
INSERT INTO doctors (name, specialty, contact, email, address) VALUES
('Dr. Martin', 'Consultation générale', '0600000001', 'dr.martin@clinique.ma', 'Cabinet Médical - Casablanca'),
('Dr. Bernard', 'Suivi', '0600000002', 'dr.bernard@clinique.ma', 'Cabinet Médical - Rabat'),
('Dr. Dubois', 'Urgence', '0600000003', 'dr.dubois@clinique.ma', 'Service Urgences - Casablanca'),
('Dr. Laurent', 'Consultation générale', '0600000004', 'dr.laurent@clinique.ma', 'Cabinet Médical - Marrakech'),
('Dr. Petit', 'Spécialité', '0600000005', 'dr.petit@clinique.ma', 'Service Pédiatrie - Casablanca');

-- Création d'une table pour les modèles de diagnostic si elle n'existe pas
CREATE TABLE IF NOT EXISTS diagnostic_templates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    created_by INT,
    created_date DATE,
    last_modified DATE,
    usage_count INT DEFAULT 0,
    FOREIGN KEY (created_by) REFERENCES doctors(id)
);

-- Insertion des modèles de diagnostic (avec IGNORE pour éviter les doublons)
INSERT IGNORE INTO diagnostic_templates (name, category, created_by, created_date, last_modified, usage_count) 
SELECT 
    'Consultation générale',
    'Consultation générale',
    (SELECT id FROM doctors WHERE name = 'Dr. Martin'),
    '2024-03-15',
    '2024-03-15',
    125
UNION ALL
SELECT 
    'Suivi diabète',
    'Suivi',
    (SELECT id FROM doctors WHERE name = 'Dr. Bernard'),
    '2024-03-12',
    '2024-03-12',
    45
UNION ALL
SELECT 
    'Urgence cardiaque',
    'Urgence',
    (SELECT id FROM doctors WHERE name = 'Dr. Dubois'),
    '2024-03-10',
    '2024-03-10',
    12
UNION ALL
SELECT 
    'Bilan annuel',
    'Consultation générale',
    (SELECT id FROM doctors WHERE name = 'Dr. Laurent'),
    '2024-03-08',
    '2024-03-08',
    89
UNION ALL
SELECT 
    'Consultation pédiatrique',
    'Spécialité',
    (SELECT id FROM doctors WHERE name = 'Dr. Petit'),
    '2024-03-05',
    '2024-03-05',
    67; 