USE medecin_db;

-- Ajout de médecins marocains
INSERT INTO doctors (name, specialty_id, phone, email, address) VALUES
('Dr. Mohammed El Alami', 1, '0661234567', 'elalami.med@gmail.com', 'Avenue Hassan II, N°15, Rabat'),
('Dr. Fatima Zahra Benali', 2, '0662345678', 'benali.fz@gmail.com', 'Rue Mohammed V, N°23, Casablanca'),
('Dr. Ahmed Benjelloun', 3, '0663456789', 'benjelloun.ahmed@gmail.com', 'Boulevard Zerktouni, N°45, Marrakech'),
('Dr. Laila Tazi', 4, '0664567890', 'tazi.laila@gmail.com', 'Rue Ibn Sina, N°8, Fès'),
('Dr. Karim Idrissi', 5, '0665678901', 'idrissi.karim@gmail.com', 'Avenue Mohammed VI, N°32, Tanger'),
('Dr. Samira El Ouazzani', 1, '0666789012', 'elouazzani.samira@gmail.com', 'Rue Al Farabi, N°12, Meknès'),
('Dr. Youssef El Mansouri', 2, '0667890123', 'elmansouri.y@gmail.com', 'Avenue des FAR, N°55, Agadir'),
('Dr. Nadia Berrada', 3, '0668901234', 'berrada.nadia@gmail.com', 'Boulevard Moulay Ismail, N°17, Oujda');

-- Ajout de patients marocains
INSERT INTO patients (name, birth_date, gender, phone, email, address, blood_type, allergies) VALUES
('Rachid El Amrani', '1980-05-15', 'M', '0671234567', 'elamrani.r@gmail.com', 'Rue Atlas, N°9, Rabat', 'O+', 'Aucune'),
('Amina Benjelloun', '1992-08-23', 'F', '0672345678', 'benjelloun.amina@gmail.com', 'Avenue Hassan II, N°28, Casablanca', 'A+', 'Pénicilline'),
('Hassan El Fassi', '1975-03-10', 'M', '0673456789', 'elfassi.h@gmail.com', 'Rue Ibn Rochd, N°14, Marrakech', 'B+', NULL),
('Leila Bennani', '1988-11-30', 'F', '0674567890', 'bennani.leila@gmail.com', 'Boulevard Mohammed V, N°33, Fès', 'AB+', 'Aspirine'),
('Omar El Kabbaj', '1995-07-20', 'M', '0675678901', 'elkabbaj.omar@gmail.com', 'Avenue des FAR, N°41, Tanger', 'O-', NULL),
('Safae El Alaoui', '1983-04-12', 'F', '0676789012', 'elalaoui.safae@gmail.com', 'Rue Al Wahda, N°7, Meknès', 'A-', 'Lactose'),
('Kamal Ziani', '1970-09-25', 'M', '0677890123', 'ziani.kamal@gmail.com', 'Boulevard Zerktouni, N°19, Agadir', 'B-', NULL),
('Houda El Gharbi', '1990-01-18', 'F', '0678901234', 'elgharbi.houda@gmail.com', 'Avenue Mohammed VI, N°22, Oujda', 'AB-', 'Fruits de mer'),
('Younes El Idrissi', '1987-06-05', 'M', '0679012345', 'elidrissi.y@gmail.com', 'Rue Al Massira, N°16, Rabat', 'O+', NULL),
('Salma Chraibi', '1993-12-08', 'F', '0670123456', 'chraibi.salma@gmail.com', 'Avenue Hassan II, N°37, Casablanca', 'A+', 'Gluten');

-- Ajout de rendez-vous
INSERT INTO appointments (patient_id, doctor_id, date_time, type, reason, status) VALUES
(1, 1, NOW() + INTERVAL 1 DAY, 'Consultation', 'Consultation générale', 'scheduled'),
(2, 2, NOW() + INTERVAL 2 DAY, 'Suivi', 'Suivi cardiaque', 'confirmed'),
(3, 3, NOW() + INTERVAL 3 DAY, 'Contrôle', 'Contrôle pédiatrique', 'scheduled'),
(4, 4, NOW() + INTERVAL 4 DAY, 'Consultation', 'Problème de peau', 'confirmed'),
(5, 5, NOW() + INTERVAL 5 DAY, 'Consultation', 'Examen de la vue', 'scheduled'),
(6, 6, NOW() + INTERVAL 6 DAY, 'Urgence', 'Forte fièvre', 'confirmed'),
(7, 7, NOW() + INTERVAL 7 DAY, 'Suivi', 'Suivi tension artérielle', 'scheduled'),
(8, 8, NOW() + INTERVAL 8 DAY, 'Contrôle', 'Vaccination', 'confirmed');

-- Ajout de dossiers médicaux
INSERT INTO medical_records (patient_id, doctor_id, date_time, diagnosis, treatment, prescription, notes) VALUES
(1, 1, NOW(), 'Grippe saisonnière', 'Repos et hydratation', 'Paracétamol 1000mg', 'Patient à revoir dans une semaine'),
(2, 2, NOW() - INTERVAL 1 DAY, 'Hypertension légère', 'Régime sans sel', 'Amlodipine 5mg', 'Contrôle tension régulier'),
(3, 3, NOW() - INTERVAL 2 DAY, 'Bronchite', 'Repos et sirop', 'Antibiotiques', 'Amélioration notable'),
(4, 4, NOW() - INTERVAL 3 DAY, 'Eczéma', 'Crème hydratante', 'Cortisone locale', 'Éviter les allergènes'),
(5, 5, NOW() - INTERVAL 4 DAY, 'Myopie légère', 'Port de lunettes', 'Prescription lunettes', 'Contrôle annuel recommandé');

-- Ajout de prescriptions
INSERT INTO prescriptions (medical_record_id, medication_name, dosage, frequency, duration, notes) VALUES
(1, 'Doliprane', '1000mg', '3 fois par jour', '5 jours', 'À prendre après les repas'),
(1, 'Vitamine C', '500mg', '1 fois par jour', '10 jours', 'Le matin'),
(2, 'Amlodipine', '5mg', '1 fois par jour', '30 jours', 'Le soir'),
(3, 'Augmentin', '1g', '2 fois par jour', '7 jours', 'Pendant les repas'),
(4, 'Dermoval', 'Application locale', '2 fois par jour', '15 jours', 'Sur les zones affectées'),
(5, 'Collyre', '1 goutte', '3 fois par jour', '7 jours', 'Dans chaque œil'); 