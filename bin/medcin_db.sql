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
