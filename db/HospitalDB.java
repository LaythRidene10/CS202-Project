package db;

import model.Appointment;
import model.Doctor;
import model.Patient;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * JDBC layer — connects to MySQL and provides CRUD for patients, doctors, appointments.
 *
 * Setup:
 *   CREATE DATABASE hospital_db;
 *   (tables are auto-created on first run via initTables())
 *
 * Driver: add mysql-connector-j-x.x.x.jar to your classpath.
 */
public class HospitalDB {

    private static final String URL  = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER = "root";
    private static final String PASS = "password"; // change to your MySQL password

    private Connection conn;

    public HospitalDB() throws SQLException {
        conn = DriverManager.getConnection(URL, USER, PASS);
        initTables();
        System.out.println("[DB] Connected to hospital_db.");
    }

    private void initTables() throws SQLException {
        String patients =
            "CREATE TABLE IF NOT EXISTS patients (" +
            "  id         INT PRIMARY KEY," +
            "  name       VARCHAR(100)," +
            "  phone      VARCHAR(20)," +
            "  email      VARCHAR(100)," +
            "  blood_type VARCHAR(5)" +
            ")";
        String doctors =
            "CREATE TABLE IF NOT EXISTS doctors (" +
            "  id         INT PRIMARY KEY," +
            "  name       VARCHAR(100)," +
            "  phone      VARCHAR(20)," +
            "  email      VARCHAR(100)," +
            "  specialty  VARCHAR(100)," +
            "  license_id VARCHAR(50)" +
            ")";
        String appointments =
            "CREATE TABLE IF NOT EXISTS appointments (" +
            "  id           INT PRIMARY KEY AUTO_INCREMENT," +
            "  patient_id   INT," +
            "  doctor_id    INT," +
            "  date_time    DATETIME," +
            "  status       VARCHAR(20)," +
            "  notes        TEXT," +
            "  FOREIGN KEY (patient_id) REFERENCES patients(id)," +
            "  FOREIGN KEY (doctor_id)  REFERENCES doctors(id)" +
            ")";
        try (Statement st = conn.createStatement()) {
            st.execute(patients);
            st.execute(doctors);
            st.execute(appointments);
        }
    }

    // ── Patients ─────────────────────────────────────────────────────────────

    public void savePatient(Patient p) throws SQLException {
        String sql = "INSERT INTO patients(id,name,phone,email,blood_type) VALUES(?,?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE name=VALUES(name), phone=VALUES(phone), " +
                     "email=VALUES(email), blood_type=VALUES(blood_type)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());    ps.setString(2, p.getName());
            ps.setString(3, p.getPhone()); ps.setString(4, p.getEmail());
            ps.setString(5, p.getBloodType());
            ps.executeUpdate();
        }
    }

    public Patient getPatient(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Patient(rs.getInt("id"), rs.getString("name"),
                        rs.getString("phone"), rs.getString("email"), rs.getString("blood_type"));
        }
        return null;
    }

    public void deletePatient(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM patients WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    public void listAllPatients() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM patients")) {
            System.out.println("\n── All Patients ──────────────────────────");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  #%-4d %-20s %-12s %-25s Blood: %s%n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("blood_type"));
            }
            if (!any) System.out.println("  (no patients)");
            System.out.println("──────────────────────────────────────────");
        }
    }

    // ── Doctors ──────────────────────────────────────────────────────────────

    public void saveDoctor(Doctor d) throws SQLException {
        String sql = "INSERT INTO doctors(id,name,phone,email,specialty,license_id) VALUES(?,?,?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE name=VALUES(name), specialty=VALUES(specialty)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getId());    ps.setString(2, d.getName());
            ps.setString(3, d.getPhone()); ps.setString(4, d.getEmail());
            ps.setString(5, d.getSpecialty()); ps.setString(6, d.getLicenseId());
            ps.executeUpdate();
        }
    }

    public Doctor getDoctor(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM doctors WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Doctor(rs.getInt("id"), rs.getString("name"),
                        rs.getString("phone"), rs.getString("email"),
                        rs.getString("specialty"), rs.getString("license_id"));
        }
        return null;
    }

    public void listAllDoctors() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM doctors")) {
            System.out.println("\n── All Doctors ───────────────────────────");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  #%-4d %-20s %-18s License: %s%n",
                        rs.getInt("id"), rs.getString("name"),
                        rs.getString("specialty"), rs.getString("license_id"));
            }
            if (!any) System.out.println("  (no doctors)");
            System.out.println("──────────────────────────────────────────");
        }
    }

    // ── Appointments ─────────────────────────────────────────────────────────

    public void saveAppointment(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments(id,patient_id,doctor_id,date_time,status,notes) " +
                     "VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE status=VALUES(status), notes=VALUES(notes)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getId());         ps.setInt(2, a.getPatient().getId());
            ps.setInt(3, a.getDoctor().getId());
            ps.setTimestamp(4, Timestamp.valueOf(a.getDateTime()));
            ps.setString(5, a.getStatus().name()); ps.setString(6, a.getNotes());
            ps.executeUpdate();
        }
    }

    public void listAppointmentsByDoctor(int doctorId) throws SQLException {
        String sql = "SELECT a.id, p.name AS pname, a.date_time, a.status, a.notes " +
                     "FROM appointments a JOIN patients p ON a.patient_id=p.id " +
                     "WHERE a.doctor_id=? ORDER BY a.date_time";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n── Appointments for Doctor #" + doctorId + " ──────");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  #%-4d %-20s %s  [%s]  %s%n",
                        rs.getInt("id"), rs.getString("pname"),
                        rs.getTimestamp("date_time").toLocalDateTime(),
                        rs.getString("status"), rs.getString("notes"));
            }
            if (!any) System.out.println("  (no appointments)");
            System.out.println("──────────────────────────────────────────");
        }
    }

    public boolean hasConflict(int doctorId, LocalDateTime dt) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND date_time=? AND status='SCHEDULED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId); ps.setTimestamp(2, Timestamp.valueOf(dt));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) conn.close();
    }
}
