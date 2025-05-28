package pfe.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pfe.service.RendezVous;
import pfe.service.User; // Assuming User object might be needed

public class RendezVousDAO {

    // Method to get patient_id based on users.id using the new patient_id column
    private int getPatientIdByUserId(String userId) {
        String sql = "SELECT patient_id FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int userIdInt;
            try {
                userIdInt = Integer.parseInt(userId);
            } catch (NumberFormatException e) {
                System.err.println("Invalid userId format: " + userId);
                return -1;
            }
            
            pstmt.setInt(1, userIdInt);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int patientId = rs.getInt("patient_id");
                    if (rs.wasNull()) {
                        // The user exists but doesn't have a patient_id (e.g., admin)
                        System.err.println("User ID " + userId + " does not have an associated patient ID.");
                        return -1; 
                    }
                    return patientId;
                } else {
                    // User ID not found in users table
                    System.err.println("User ID " + userId + " not found in users table.");
                    return -1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting patient ID for user ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public boolean scheduleRendezVous(String userId, String doctorName, String dateTime, String reason) {
        // First, get the doctor ID from the name
        int doctorId = getDoctorIdByName(doctorName);
        if (doctorId == -1) {
            System.err.println("Doctor not found: " + doctorName);
            return false; // Doctor not found
        }

        // Second, get the patient ID using the new method
        int patientId = getPatientIdByUserId(userId);
        if (patientId == -1) {
            System.err.println("Could not schedule: Failed to determine patient ID for user ID: " + userId);
            return false; // Patient ID could not be determined or user is not a patient
        }

        // SQL includes the 'reason' column
        String sql = "INSERT INTO rendezvous (patient_id, doctor_id, date_time, reason) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId); // Use the retrieved patientId
            pstmt.setInt(2, doctorId);
            pstmt.setString(3, dateTime);
            pstmt.setString(4, reason); // Add the reason
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error scheduling rendez-vous: " + e.getMessage());
            // Check specifically for foreign key constraint violation
            if (e.getSQLState().startsWith("23")) { 
                 System.err.println("Possible foreign key violation. Check if patient_id " + patientId + " exists in patients table and doctor_id " + doctorId + " exists in doctors table.");
            }
            e.printStackTrace();
            return false;
        }
    }

    // Method to get appointments for a user (patient)
    public List<RendezVous> getRendezVousByUser (String userId) {
        List<RendezVous> rendezVousList = new ArrayList<>();
        
        // Get the corresponding patient ID using the new method
        int patientId = getPatientIdByUserId(userId);
        if (patientId == -1) {
            System.err.println("Could not get appointments: Failed to determine patient ID for user ID: " + userId);
            return rendezVousList; // Return empty list if user is not a patient or not found
        }

        // SQL query includes the 'reason' column and joins with doctors
        String sql = "SELECT r.id, r.patient_id, r.doctor_id, r.date_time, r.status, r.reason, d.name AS doctor_name " +
                     "FROM rendezvous r JOIN doctors d ON r.doctor_id = d.id " +
                     "WHERE r.patient_id = ? ORDER BY r.date_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId); // Use the retrieved patientId
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rendezVousList.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting rendez-vous by user (patient_id: " + patientId + "): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error mapping result set for user (patient_id: " + patientId + "): " + e.getMessage());
            e.printStackTrace();
        }
        return rendezVousList;
    }

    // Helper method to map ResultSet to RendezVous object
    private RendezVous mapResultSetToAppointment(ResultSet rs) throws SQLException {
        String doctorName = rs.getString("doctor_name"); 
        return new RendezVous(
                rs.getInt("id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                doctorName, 
                rs.getString("date_time"),
                rs.getString("reason"),
                rs.getString("status")
        );
    }

    // --- Other existing methods (reschedule, cancel, hasConflict, etc.) --- 

    public void rescheduleAppointment(int appointmentId, String newDateTime) {
        String sql = "UPDATE rendezvous SET date_time = ?, status = \"scheduled\" WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newDateTime);
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
             System.err.println("Error rescheduling appointment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cancelAppointment(int appointmentId) {
        String sql = "UPDATE rendezvous SET status = \"canceled\" WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
             System.err.println("Error canceling appointment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean hasConflict(int doctorId, String dateTime) {
        String sql = "SELECT COUNT(*) FROM rendezvous WHERE doctor_id = ? AND date_time = ? AND status = \"scheduled\"";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, dateTime);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
             System.err.println("Error checking for conflict: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<RendezVous> getAppointmentsForDoctor(int doctorId) {
        List<RendezVous> appointments = new ArrayList<>();
        String sql = "SELECT r.*, d.name as doctor_name, p.name as patient_name " +
                     "FROM rendezvous r " +
                     "JOIN doctors d ON r.doctor_id = d.id " +
                     "JOIN patients p ON r.patient_id = p.id " +
                     "WHERE r.doctor_id = ? ORDER BY r.date_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                     appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
             System.err.println("Error getting appointments for doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    public List<RendezVous> getAppointmentsForPatient(int patientId) {
        List<RendezVous> appointments = new ArrayList<>();
         String sql = "SELECT r.*, d.name as doctor_name " +
                     "FROM rendezvous r JOIN doctors d ON r.doctor_id = d.id " +
                     "WHERE r.patient_id = ? ORDER BY r.date_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
             System.err.println("Error getting appointments for patient: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    public void updateStatus(int appointmentId, String status) {
        String sql = "UPDATE rendezvous SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
             System.err.println("Error updating status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get doctor names from the 'doctors' table
    public List<String> getAllDoctorNames() {
        List<String> doctorNames = new ArrayList<>();
        String sql = "SELECT name FROM doctors";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                doctorNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all doctor names: " + e.getMessage());
            e.printStackTrace();
        }
        return doctorNames;
    }
    
    // Helper method to get doctor name by ID
    private String getDoctorNameById(int doctorId) {
        String sql = "SELECT name FROM doctors WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting doctor name by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null; 
    }

    // Helper method to get doctor ID by name
    private int getDoctorIdByName(String doctorName) {
        String sql = "SELECT id FROM doctors WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctorName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting doctor ID by name: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Indicate not found
    }
}

