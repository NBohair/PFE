USE medecin_db;

-- Vérification des tables
SELECT 'Tables existantes:' as '';
SHOW TABLES;

-- Vérification de la structure de chaque table
SELECT 'Structure de la table administrators:' as '';
DESCRIBE administrators;

SELECT 'Structure de la table doctors:' as '';
DESCRIBE doctors;

SELECT 'Structure de la table patients:' as '';
DESCRIBE patients;

SELECT 'Structure de la table appointments:' as '';
DESCRIBE appointments;

SELECT 'Structure de la table medical_records:' as '';
DESCRIBE medical_records;

SELECT 'Structure de la table prescriptions:' as '';
DESCRIBE prescriptions;

SELECT 'Structure de la table specialties:' as '';
DESCRIBE specialties;

-- Vérification des index
SELECT 'Index existants:' as '';
SELECT DISTINCT
    TABLE_NAME,
    INDEX_NAME,
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) as COLUMNS
FROM information_schema.statistics
WHERE TABLE_SCHEMA = 'medecin_db'
GROUP BY TABLE_NAME, INDEX_NAME;

-- Vérification des clés étrangères
SELECT 'Clés étrangères:' as '';
SELECT 
    TABLE_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'medecin_db'
AND REFERENCED_TABLE_NAME IS NOT NULL;

-- Vérification des vues
SELECT 'Vues existantes:' as '';
SHOW FULL TABLES WHERE TABLE_TYPE LIKE 'VIEW';

-- Vérification des données
SELECT 'Nombre d''enregistrements dans chaque table:' as '';
SELECT 'Administrators:' as Table_Name, COUNT(*) as Count FROM administrators
UNION ALL
SELECT 'Doctors:', COUNT(*) FROM doctors
UNION ALL
SELECT 'Patients:', COUNT(*) FROM patients
UNION ALL
SELECT 'Appointments:', COUNT(*) FROM appointments
UNION ALL
SELECT 'Medical Records:', COUNT(*) FROM medical_records
UNION ALL
SELECT 'Prescriptions:', COUNT(*) FROM prescriptions
UNION ALL
SELECT 'Specialties:', COUNT(*) FROM specialties; 