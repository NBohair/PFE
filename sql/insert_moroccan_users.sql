USE medecin_db;

-- Insertion des médecins marocains
INSERT INTO doctors (name, specialty, contact, email, address) VALUES
('Dr. Mohammed El Amrani', 'Médecine générale', '0661234567', 'elamrani.med@clinique.ma', 'Avenue Hassan II, N°15, Casablanca'),
('Dr. Fatima Bennis', 'Cardiologie', '0662345678', 'bennis.fatima@clinique.ma', 'Rue Mohammed V, N°23, Rabat'),
('Dr. Ahmed Tazi', 'Pédiatrie', '0663456789', 'tazi.ahmed@clinique.ma', 'Boulevard Zerktouni, N°45, Marrakech'),
('Dr. Samira El Fassi', 'Dermatologie', '0664567890', 'elfassi.samira@clinique.ma', 'Rue Ibn Sina, N°8, Fès'),
('Dr. Karim Benjelloun', 'Ophtalmologie', '0665678901', 'benjelloun.karim@clinique.ma', 'Avenue Mohammed VI, N°32, Tanger'),
('Dr. Leila El Alami', 'Médecine générale', '0666789012', 'elalami.leila@clinique.ma', 'Rue Al Farabi, N°12, Agadir'),
('Dr. Youssef El Idrissi', 'Cardiologie', '0667890123', 'elidrissi.youssef@clinique.ma', 'Avenue des FAR, N°55, Casablanca'),
('Dr. Nadia Berrada', 'Pédiatrie', '0668901234', 'berrada.nadia@clinique.ma', 'Boulevard Moulay Ismail, N°17, Rabat');

-- Insertion des administrateurs
INSERT INTO users (username, password, role, first_name, last_name, email, created_at) VALUES
('admin.rachid', '1234', 'admin', 'Rachid', 'El Mansouri', 'elmansouri.rachid@admin.ma', CURRENT_TIMESTAMP),
('admin.amina', '1234', 'admin', 'Amina', 'Ziani', 'ziani.amina@admin.ma', CURRENT_TIMESTAMP),
('admin.hassan', '1234', 'admin', 'Hassan', 'El Ouazzani', 'elouazzani.hassan@admin.ma', CURRENT_TIMESTAMP);

-- Insertion des patients
INSERT INTO patients (name, birth_date, contact, email, address) VALUES
('Rachid El Amrani', '1980-05-15', '0671234567', 'elamrani.r@gmail.ma', 'Rue Atlas, N°9, Casablanca'),
('Amina Benjelloun', '1992-08-23', '0672345678', 'benjelloun.amina@gmail.ma', 'Avenue Hassan II, N°28, Rabat'),
('Hassan El Fassi', '1975-03-10', '0673456789', 'elfassi.h@gmail.ma', 'Rue Ibn Rochd, N°14, Marrakech'),
('Leila Bennani', '1988-11-30', '0674567890', 'bennani.leila@gmail.ma', 'Boulevard Mohammed V, N°33, Fès'),
('Omar El Kabbaj', '1995-07-20', '0675678901', 'elkabbaj.omar@gmail.ma', 'Avenue des FAR, N°41, Tanger'),
('Safae El Alaoui', '1983-04-12', '0676789012', 'elalaoui.safae@gmail.ma', 'Rue Al Wahda, N°7, Meknès'),
('Kamal Ziani', '1970-09-25', '0677890123', 'ziani.kamal@gmail.ma', 'Boulevard Zerktouni, N°19, Agadir'),
('Houda El Gharbi', '1990-01-18', '0678901234', 'elgharbi.houda@gmail.ma', 'Avenue Mohammed VI, N°22, Oujda');

-- Création de comptes utilisateurs pour les nouveaux médecins
INSERT INTO users (username, password, role, first_name, last_name, email, created_at) VALUES
('dr.elamrani', '1234', 'user', 'Mohammed', 'El Amrani', 'elamrani.med@clinique.ma', CURRENT_TIMESTAMP),
('dr.bennis', '1234', 'user', 'Fatima', 'Bennis', 'bennis.fatima@clinique.ma', CURRENT_TIMESTAMP),
('dr.tazi', '1234', 'user', 'Ahmed', 'Tazi', 'tazi.ahmed@clinique.ma', CURRENT_TIMESTAMP),
('dr.elfassi', '1234', 'user', 'Samira', 'El Fassi', 'elfassi.samira@clinique.ma', CURRENT_TIMESTAMP),
('dr.benjelloun', '1234', 'user', 'Karim', 'Benjelloun', 'benjelloun.karim@clinique.ma', CURRENT_TIMESTAMP),
('dr.elalami', '1234', 'user', 'Leila', 'El Alami', 'elalami.leila@clinique.ma', CURRENT_TIMESTAMP),
('dr.elidrissi', '1234', 'user', 'Youssef', 'El Idrissi', 'elidrissi.youssef@clinique.ma', CURRENT_TIMESTAMP),
('dr.berrada', '1234', 'user', 'Nadia', 'Berrada', 'berrada.nadia@clinique.ma', CURRENT_TIMESTAMP);

-- Création de comptes utilisateurs pour les nouveaux patients
INSERT INTO users (username, password, role, first_name, last_name, email, created_at) VALUES
('patient.rachid', '1234', 'user', 'Rachid', 'El Amrani', 'elamrani.r@gmail.ma', CURRENT_TIMESTAMP),
('patient.amina', '1234', 'user', 'Amina', 'Benjelloun', 'benjelloun.amina@gmail.ma', CURRENT_TIMESTAMP),
('patient.hassan', '1234', 'user', 'Hassan', 'El Fassi', 'elfassi.h@gmail.ma', CURRENT_TIMESTAMP),
('patient.leila', '1234', 'user', 'Leila', 'Bennani', 'bennani.leila@gmail.ma', CURRENT_TIMESTAMP),
('patient.omar', '1234', 'user', 'Omar', 'El Kabbaj', 'elkabbaj.omar@gmail.ma', CURRENT_TIMESTAMP),
('patient.safae', '1234', 'user', 'Safae', 'El Alaoui', 'elalaoui.safae@gmail.ma', CURRENT_TIMESTAMP),
('patient.kamal', '1234', 'user', 'Kamal', 'Ziani', 'ziani.kamal@gmail.ma', CURRENT_TIMESTAMP),
('patient.houda', '1234', 'user', 'Houda', 'El Gharbi', 'elgharbi.houda@gmail.ma', CURRENT_TIMESTAMP); 