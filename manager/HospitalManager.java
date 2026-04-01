package manager;

import db.HospitalDB;
import exceptions.AppointmentConflictException;
import exceptions.PatientNotFoundException;
import model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Core business logic layer.
 * Uses Java Collections and Generics extensively:
 *   - List<Patient>  — patient registry
 *   - List<Doctor>   — doctor registry
 *   - Map<Integer, List<Appointment>>  — doctor -> their appointments
 *   - Queue<Appointment>               — pending appointments (FIFO)
 */
public class HospitalManager {

    private final List<Patient>   patients    = new ArrayList<>();
    private final List<Doctor>    doctors     = new ArrayList<>();
    private final Map<Integer, List<Appointment>> doctorSchedule = new HashMap<>();
    private final Queue<Appointment> pendingQueue = new LinkedList<>();

    private final HospitalDB db;
    private int apptCounter = 1;

    public HospitalManager(HospitalDB db) {
        this.db = db;
    }

    // ── Patients ─────────────────────────────────────────────────────────────

    public void addPatient(Patient p) throws SQLException {
        patients.add(p);
        db.savePatient(p);
        System.out.println("[+] Patient added: " + p.getName());
    }

    public Patient findPatient(int id) throws PatientNotFoundException {
        return patients.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    public void removePatient(int id) throws PatientNotFoundException, SQLException {
        Patient p = findPatient(id);
        patients.remove(p);
        db.deletePatient(id);
        System.out.println("[-] Patient removed: " + p.getName());
    }

    public List<Patient> getAllPatients() { return Collections.unmodifiableList(patients); }

    // ── Doctors ──────────────────────────────────────────────────────────────

    public void addDoctor(Doctor d) throws SQLException {
        doctors.add(d);
        doctorSchedule.put(d.getId(), new ArrayList<>());
        db.saveDoctor(d);
        System.out.println("[+] Doctor added: Dr. " + d.getName() + " (" + d.getSpecialty() + ")");
    }

    public Doctor findDoctor(int id) {
        return doctors.stream().filter(d -> d.getId() == id).findFirst().orElse(null);
    }

    public List<Doctor> getAllDoctors() { return Collections.unmodifiableList(doctors); }

    // ── Appointments ─────────────────────────────────────────────────────────

    public Appointment bookAppointment(int patientId, int doctorId, LocalDateTime dt)
            throws PatientNotFoundException, AppointmentConflictException, SQLException {

        Patient p = findPatient(patientId);
        Doctor  d = findDoctor(doctorId);
        if (d == null) throw new IllegalArgumentException("Doctor #" + doctorId + " not found.");

        if (db.hasConflict(doctorId, dt))
            throw new AppointmentConflictException(dt);

        Appointment appt = new Appointment(apptCounter++, p, d, dt);
        doctorSchedule.computeIfAbsent(doctorId, k -> new ArrayList<>()).add(appt);
        pendingQueue.offer(appt);
        db.saveAppointment(appt);
        System.out.println("[+] Appointment booked: " + appt);
        return appt;
    }

    public void cancelAppointment(int apptId) throws SQLException {
        for (List<Appointment> list : doctorSchedule.values()) {
            for (Appointment a : list) {
                if (a.getId() == apptId) {
                    a.setStatus(Appointment.Status.CANCELLED);
                    pendingQueue.remove(a);
                    db.saveAppointment(a);
                    System.out.println("[-] Appointment #" + apptId + " cancelled.");
                    return;
                }
            }
        }
        System.out.println("[!] Appointment #" + apptId + " not found.");
    }

    public Appointment processNextPending() {
        Appointment next = pendingQueue.poll();
        if (next == null) { System.out.println("[!] No pending appointments."); return null; }
        next.setStatus(Appointment.Status.COMPLETED);
        System.out.println("[✓] Processed: " + next);
        return next;
    }

    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        return Collections.unmodifiableList(
                doctorSchedule.getOrDefault(doctorId, Collections.emptyList()));
    }

    public List<Appointment> getAppointmentsForPatient(int patientId) {
        List<Appointment> result = new ArrayList<>();
        for (List<Appointment> list : doctorSchedule.values())
            for (Appointment a : list)
                if (a.getPatient().getId() == patientId) result.add(a);
        return result;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> all = new ArrayList<>();
        doctorSchedule.values().forEach(all::addAll);
        return all;
    }

    public HospitalDB getDb() { return db; }
}
