package pfe.service;

public class RendezVous {
    private int id;
    private int patientId;
    private int doctorId;
    private String doctorName;
    private String dateTime;
    private String reason;
    private String status; // e.g., "upcoming", "completed", "cancelled"

    // Constructeur par défaut
    public RendezVous() {}

    // Constructeur avec paramètres
    public RendezVous(int id, int patientId, int doctorId, String doctorName, String dateTime, String reason, String status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.dateTime = dateTime;
        this.reason = reason;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
