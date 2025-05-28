USE medecin_db;

-- Requête pour récupérer les modèles de diagnostic avec les informations des médecins
SELECT 
    dt.id,
    dt.name AS template_name,
    dt.category,
    d.name AS doctor_name,
    dt.created_date,
    dt.last_modified,
    dt.usage_count,
    dt.description
FROM 
    diagnostic_templates dt
LEFT JOIN 
    doctors d ON dt.created_by = d.id
ORDER BY 
    dt.id ASC; 