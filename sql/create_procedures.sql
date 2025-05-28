USE medecin_db;

DELIMITER //

CREATE PROCEDURE create_medical_folder(
    IN p_patient_id INT,
    IN p_doctor_id INT,
    IN p_creation_date DATE,
    IN p_status VARCHAR(50)
)
BEGIN
    INSERT INTO medical_folders (
        patient_id,
        doctor_id,
        creation_date,
        status,
        last_modified
    ) VALUES (
        p_patient_id,
        p_doctor_id,
        p_creation_date,
        p_status,
        NOW()
    );
END //

DELIMITER ; 