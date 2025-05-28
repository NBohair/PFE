USE medecin_db;

-- Requête pour récupérer l'historique des diagnostics avec les informations des patients et médecins
SELECT 
    d.id,
    p.name AS patient_name,
    doc.name AS doctor_name,
    d.date_time AS diagnostic_date,
    d.diagnosis
FROM 
    diagnostics d
LEFT JOIN 
    patients p ON d.patient_id = p.id
LEFT JOIN 
    doctors doc ON d.doctor_id = doc.id
WHERE 
    d.date_time BETWEEN '2025-05-29' AND '2025-05-30'
    AND (doc.id = 8 OR doc.id IS NULL)
ORDER BY 
    d.date_time DESC; 