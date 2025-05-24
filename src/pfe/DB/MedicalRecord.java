package pfe.DB;

import pfe.DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {
    private static final String SELECT_ALL = "SELECT id, patient_id, record_date, notes FROM medical_records";
    private static final String INSERT_RECORD = "INSERT INTO medical_records (patient_id, record_date, notes) VALUES (?, ?, ?)";
    private static final String UPDATE_RECORD = "UPDATE medical_records SET record_date = ?, notes = ? WHERE id = ?";
    private static final String DELETE_RECORD = "DELETE FROM medical_records WHERE id = ?";
    private int id; // Assuming ID is an integer
    private String patientId;
    private String recordDate; // Assuming this is a String, could also be a Date type
    private String notes;

    // Constructor
    public MedicalRecord(int id, String patientId, String recordDate, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.recordDate = recordDate;
        this.notes = notes;
    }
    
    // Getters
    public int getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDate() {
        return recordDate; // Assuming this is the date representation
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public static List<MedicalRecord> getAllRecords() {
        List<MedicalRecord> list = new ArrayList<>();
        try (Connection conn = pfe.DB.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new MedicalRecord(
                    rs.getInt("id"),
                    rs.getString("patient_id"),
                    rs.getString("record_date"),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean createOrUpdateRecord(String patientId, String date, String notes) {
        try (Connection conn = pfe.DB.DBConnection.getConnection()) {
            // Attempt insert first
            try (PreparedStatement ps = conn.prepareStatement(INSERT_RECORD)) {
                ps.setString(1, patientId);
                ps.setString(2, date);
                ps.setString(3, notes);
                return ps.executeUpdate() == 1;
            } catch (SQLException insertEx) {
                // If insert fails (e.g., duplicate), attempt update
                try (PreparedStatement ps2 = conn.prepareStatement(UPDATE_RECORD)) {
                    ps2.setString(1, date);
                    ps2.setString(2, notes);
                    ps2.setString(3, patientId);
                    return ps2.executeUpdate() == 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteRecord(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_RECORD)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}