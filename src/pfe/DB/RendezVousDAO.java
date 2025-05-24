package pfe.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pfe.service.RendezVous;

public class RendezVousDAO {

    
	public boolean scheduleRendezVous(String userId, String doctor, String dateTime) {
        String sql = "INSERT INTO rendezvous (patient_id, doctor_id, date_time) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, doctor); // You may need to convert doctor name to ID
            pstmt.setString(3, dateTime);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void rescheduleAppointment(int appointmentId, String newDateTime) {
        String sql = "UPDATE rendezvous SET date_time = ?, status = 'upcoming' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newDateTime);
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelAppointment(int appointmentId) {
        String sql = "UPDATE rendezvous SET status = 'cancelled' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasConflict(int doctorId, String dateTime) {
        String sql = "SELECT COUNT(*) FROM rendezvous WHERE doctor_id = ? AND date_time = ? AND status = 'upcoming'";
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
            e.printStackTrace();
        }
        return false;
    }

    public List<RendezVous> getAppointmentsForDoctor(int doctorId) {
        List<RendezVous> appointments = new ArrayList<>();
        String sql = "SELECT * FROM rendezvous WHERE doctor_id = ? ORDER BY date_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public List<RendezVous> getAppointmentsForPatient(int patientId) {
        List<RendezVous> appointments = new ArrayList<>();
        String sql = "SELECT * FROM rendezvous WHERE patient_id = ? ORDER BY date_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
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
            e.printStackTrace();
        }
    }

    private RendezVous mapResultSetToAppointment(ResultSet rs) throws SQLException {
        return new RendezVous(
                rs.getInt("id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                null, // doctorName can be joined from another table if needed
                rs.getString("date_time"),
                rs.getString("reason"),
                rs.getString("status")
        );
    }
    public List<String> getAllDoctorNames() {
        List<String> doctorNames = new ArrayList<>();
        String sql = "SELECT name FROM medecins"; // Adjust the SQL query as needed
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                doctorNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctorNames;
    }
    
    public List<RendezVous> getRendezVousByUser (String userId) {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String sql = "SELECT r.date_time, m.name AS doctor_name, r.reason " +
                     "FROM rendezvous r JOIN medecins m ON r.doctor_id = m.id " +
                     "WHERE r.patient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] dateTimeParts = rs.getString("date_time").split(" ");
                    String date = dateTimeParts.length > 0 ? dateTimeParts[0] : "";
                    String time = dateTimeParts.length > 1 ? dateTimeParts[1] : "";
                    rendezVousList.add(new RendezVous(
                            0, // ID to be defined if necessary
                            Integer.parseInt(userId),
                            0, // doctorId to be defined if necessary
                            rs.getString("doctor_name"),
                            date + " " + time,
                            rs.getString("reason"),
                            "upcoming" // Default status
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rendezVousList;
    }
}
