-- -- Create the database
-- CREATE DATABASE medecin_db;

-- -- Use the created database
-- USE medecin_db;

-- -- Create a table for doctors
-- CREATE TABLE doctors (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     specialty VARCHAR(100) NOT NULL,
--     contact VARCHAR(50),
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- -- Create a table for patients
-- CREATE TABLE patients (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     contact VARCHAR(50),
--     email VARCHAR(100),
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- -- Create a table for appointments (rendezvous)
-- CREATE TABLE rendezvous (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     patient_id INT,
--     doctor_id INT,
--     date_time DATETIME NOT NULL,
--     status ENUM('scheduled', 'completed', 'canceled') DEFAULT 'scheduled',
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (patient_id) REFERENCES patients(id),
--     FOREIGN KEY (doctor_id) REFERENCES doctors(id)
-- );

-- -- Create a table for users (for login purposes)
-- CREATE TABLE users (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(50) UNIQUE NOT NULL,
--     password VARCHAR(255) NOT NULL, 
--     role ENUM('admin', 'user') DEFAULT 'user',
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );
-- -- Medical Records Table
-- CREATE TABLE medical_records (ra
--                                  id INT AUTO_INCREMENT PRIMARY KEY,
--                                  patient_id INT NOT NULL,
--                                  record_date DATE NOT NULL,
--                                  notes TEXT,
--                                  FOREIGN KEY (patient_id) REFERENCES patients(id)
-- );
-- -----------------------------------------------------------------------------------------------
-- -----------------------------------------------------------------------------------------------
-- ------------------------------------------donner du data---------------------------------------
-- -----------------------------------------------------------------------------------------------
-- -----------------------------------------------------------------------------------------------
-- -- Insert Doctors
-- INSERT INTO doctors (name, specialty, contact) VALUES 
-- ('Dr. Ahmed El Amrani', 'Cardiology', '0661-234567'),
-- ('Dr. Fatima Zahra Benali', 'Dermatology', '0662-345678');

-- -- Insert Patients
-- INSERT INTO patients (name, contact, email) VALUES 
-- ('Youssef Bouzid', '0663-456789', 'youssef.bouzid@example.com'),
-- ('Amina El Idrissi', '0664-567890', 'amina.elidrissi@example.com');

-- -- Insert Users
-- INSERT INTO users (username, password, role) VALUES 
-- ('admin', 'admin123','admin'), 
-- ('user', 'user123','user');

-- -- Insert Appointments
-- INSERT INTO rendezvous (patient_id, doctor_id, date_time, status) VALUES 
-- (1, 1, '2023-10-01 10:00:00', 'scheduled'),
-- (2, 2, '2023-10-02 14:00:00', 'scheduled');

-- -- add column 

-- -- add column to users
-- ALTER TABLE users
-- ADD COLUMN first_name VARCHAR(50),
-- ADD COLUMN last_name VARCHAR(50),
-- ADD COLUMN email VARCHAR(100) UNIQUE;

-- -- data Medical Records
-- INSERT INTO medical_records (patient_id, record_date, notes) VALUES
--     (1, '2023-09-15', 'Consultation pour douleurs thoraciques. ECG réalisé. Résultat normal.'),
--     (1, '2023-11-20', 'Contrôle de routine. Tension artérielle légèrement élevée. Suivi recommandé.'),
--     (2, '2023-10-10', 'Plaintes de démangeaisons cutanées. Diagnostic : eczéma léger. Traitement prescrit.'),
--     (2, '2024-01-05', 'Suivi dermatologique. Amélioration notable après traitement. Aucun effet secondaire.');

-- Create the database
CREATE DATABASE medecin_db;

-- Use the created database
USE medecin_db;

-- Create a table for doctors
CREATE TABLE doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create a table for patients
CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create a table for appointments (rendezvous)
CREATE TABLE rendezvous (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    doctor_id INT,
    date_time DATETIME NOT NULL,
    status ENUM('scheduled', 'completed', 'canceled') DEFAULT 'scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- Create a table for users (for login purposes)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, 
    role ENUM('admin', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Medical Records Table
CREATE TABLE medical_records (ra
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 patient_id INT NOT NULL,
                                 record_date DATE NOT NULL,
                                 notes TEXT,
                                 FOREIGN KEY (patient_id) REFERENCES patients(id)
);
-----------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------
------------------------------------------donner du data---------------------------------------
-----------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------
-- Insert Doctors
INSERT INTO doctors (name, specialty, contact) VALUES 
('Dr. Ahmed El Amrani', 'Cardiology', '0661-234567'),
('Dr. Fatima Zahra Benali', 'Dermatology', '0662-345678');

-- Insert Patients
INSERT INTO patients (name, contact, email) VALUES 
('Youssef Bouzid', '0663-456789', 'youssef.bouzid@example.com'),
('Amina El Idrissi', '0664-567890', 'amina.elidrissi@example.com');

-- Insert Users
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123','admin'), 
('user', 'user123','user');

-- Insert Appointments
INSERT INTO rendezvous (patient_id, doctor_id, date_time, status) VALUES 
(1, 1, '2023-10-01 10:00:00', 'scheduled'),
(2, 2, '2023-10-02 14:00:00', 'scheduled');

-- add column 

-- add column to users
ALTER TABLE users
ADD COLUMN first_name VARCHAR(50),
ADD COLUMN last_name VARCHAR(50),
ADD COLUMN email VARCHAR(100) UNIQUE;

-- data Medical Records
INSERT INTO medical_records (patient_id, record_date, notes) VALUES
    (1, '2023-09-15', 'Consultation pour douleurs thoraciques. ECG réalisé. Résultat normal.'),
    (1, '2023-11-20', 'Contrôle de routine. Tension artérielle légèrement élevée. Suivi recommandé.'),
    (2, '2023-10-10', 'Plaintes de démangeaisons cutanées. Diagnostic : eczéma léger. Traitement prescrit.'),
    (2, '2024-01-05', 'Suivi dermatologique. Amélioration notable après traitement. Aucun effet secondaire.');

-- Create the database
CREATE DATABASE IF NOT EXISTS medecin_db;

-- Use the created database
USE medecin_db;

-- Create a table for doctors
CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create a table for patients
CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create a table for appointments (rendezvous)
-- Added the 'reason' column
CREATE TABLE IF NOT EXISTS rendezvous (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    doctor_id INT,
    date_time DATETIME NOT NULL,
    reason TEXT, -- Added reason column
    status ENUM("scheduled", "completed", "canceled") DEFAULT "scheduled",
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE SET NULL ON UPDATE CASCADE, -- Consider ON DELETE behavior
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL ON UPDATE CASCADE   -- Consider ON DELETE behavior
);

-- Create a table for users (for login purposes)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, 
    role ENUM("admin", "user", "doctor") DEFAULT "user", -- Added doctor role possibility
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    -- patient_id INT, -- This would link users to patients directly. Requires signup logic change.
    -- doctor_id INT, -- This would link users to doctors directly. Requires signup logic change.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    -- FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE SET NULL,
    -- FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL
);

-- Medical Records Table
CREATE TABLE IF NOT EXISTS medical_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    record_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE ON UPDATE CASCADE -- Cascade delete might be appropriate here
);

-- Insert Initial Data (if tables are empty) --

-- Insert Doctors (Example)
INSERT IGNORE INTO doctors (id, name, specialty, contact) VALUES 
(1, "Dr. Ahmed El Amrani", "Cardiology", "0661-234567"),
(2, "Dr. Fatima Zahra Benali", "Dermatology", "0662-345678");

-- Insert Patients (Example)
INSERT IGNORE INTO patients (id, name, contact, email) VALUES 
(1, "Youssef Bouzid", "0663-456789", "youssef.bouzid@example.com"),
(2, "Amina El Idrissi", "0664-567890", "amina.elidrissi@example.com");

-- Insert Users (Example - Passwords should be hashed in a real app)
-- Use the hashed passwords generated earlier
INSERT IGNORE INTO users (id, username, password, role, first_name, last_name, email) VALUES 
(1, "admin", "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", "admin", "Admin", "User", "admin@example.com"), 
(2, "user", "e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446", "user", "Youssef", "Bouzid", "youssef.bouzid@example.com"); -- Example linking user 2 to patient 1 via name/email

-- Add reason column if it doesn't exist (for existing databases) hta hadi ndiro if not exists
ALTER TABLE rendezvous ADD COLUMN reason VARCHAR(255);

-- Insert Appointments (Example)
INSERT IGNORE INTO rendezvous (id, patient_id, doctor_id, date_time, reason, status) VALUES 
(1, 1, 1, "2024-10-01 10:00:00", "Consultation cardiologique", "scheduled"),
(2, 2, 2, "2024-10-02 14:00:00", "Contrôle dermatologique", "scheduled");

-- Insert Medical Records (Example)
INSERT IGNORE INTO medical_records (patient_id, record_date, notes) VALUES
(1, "2023-09-15", "Consultation pour douleurs thoraciques. ECG réalisé. Résultat normal."),
(1, "2023-11-20", "Contrôle de routine. Tension artérielle légèrement élevée. Suivi recommandé."),
(2, "2023-10-10", "Plaintes de démangeaisons cutanées. Diagnostic : eczéma léger. Traitement prescrit."),
(2, "2024-01-05", "Suivi dermatologique. Amélioration notable après traitement. Aucun effet secondaire.");

-- anzido patient id if not exist mais ma3erftch why mabghach o hadik int null diyal ila kan admin maghaydarch lih id diyal patient
-- ALTER TABLE users ADD COLUMN IF NOT EXISTS patient_id INT NULL;
ALTER TABLE users ADD COLUMN  patient_id INT NULL;

INSERT IGNORE INTO users (id, username, password, role, first_name, last_name, email, patient_id) VALUES 
(1, "admin", "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", "admin", "Admin", "User", "admin@example.com", NULL), -- Admin has no patient_id
(2, "user", "e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446", "user", "Youssef", "Bouzid", "youssef.bouzid@example.com", 1); -- User 'user' (ID 2) is linked to Patient ID 1



