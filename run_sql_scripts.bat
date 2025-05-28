@echo off
mysql -u root -D medecin_db < sql/create_medical_folders.sql
mysql -u root -D medecin_db < sql/insert_test_folder.sql
echo Scripts SQL exécutés avec succès 