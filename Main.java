import db.HospitalDB;
import exceptions.AppointmentConflictException;
import exceptions.InvalidRecordException;
import exceptions.PatientNotFoundException;
import manager.HospitalManager;
import model.*;
import util.FileHandler;
import util.Serializer;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Hospital Management System — Main Menu
 *
 * Covers: Abstract classes, Interfaces, Custom Exceptions,
 *         File Handling, Java Collections + Generics,
 *         Serialization, JDBC / MySQL Database
 */
public class Main {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static HospitalManager manager;
    private static Scanner sc;

    public static void main(String[] args) {
        sc = new Scanner(System.in);

        // ── DB connection ────────────────────────────────────────────────────
        HospitalDB db;
        try {
            db = new HospitalDB();
        } catch (SQLException e) {
            System.out.println("[DB ERROR] Could not connect to MySQL: " + e.getMessage());
            System.out.println("[INFO] Running in offline mode (no database persistence).");
            db = null;
        }

        // ── Seed demo data ───────────────────────────────────────────────────
        manager = new HospitalManager(db);
        seedDemoData();

        // ── Menu loop ────────────────────────────────────────────────────────
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1"  -> addPatient();
                case "2"  -> removePatient();
                case "3"  -> listPatients();
                case "4"  -> addDoctor();
                case "5"  -> listDoctors();
                case "6"  -> bookAppointment();
                case "7"  -> cancelAppointment();
                case "8"  -> processNextAppointment();
                case "9"  -> viewDoctorAppointments();
                case "10" -> recordDiagnosis();
                case "11" -> saveMedicalRecord();
                case "12" -> loadMedicalRecord();
                case "13" -> exportAppointmentsToFile();
                case "14" -> exportPatientReport();
                case "0"  -> { running = false; System.out.println("Goodbye!"); }
                default   -> System.out.println("[!] Invalid option.");
            }
        }

        if (db != null) try { db.close(); } catch (SQLException ignored) {}
        sc.close();
    }

    // ── Menu ─────────────────────────────────────────────────────────────────

    private static void printMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║    HOSPITAL MANAGEMENT SYSTEM        ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  Patients                            ║");
        System.out.println("║   1. Add patient                     ║");
        System.out.println("║   2. Remove patient                  ║");
        System.out.println("║   3. List all patients               ║");
        System.out.println("║  Doctors                             ║");
        System.out.println("║   4. Add doctor                      ║");
        System.out.println("║   5. List all doctors                ║");
        System.out.println("║  Appointments                        ║");
        System.out.println("║   6. Book appointment                ║");
        System.out.println("║   7. Cancel appointment              ║");
        System.out.println("║   8. Process next pending            ║");
        System.out.println("║   9. View doctor's appointments (DB) ║");
        System.out.println("║  Medical Records                     ║");
        System.out.println("║  10. Record diagnosis + prescription ║");
        System.out.println("║  11. Save medical record (serialize) ║");
        System.out.println("║  12. Load medical record (deserializ)║");
        System.out.println("║  Reports                             ║");
        System.out.println("║  13. Export appointments to file     ║");
        System.out.println("║  14. Export patient report to file   ║");
        System.out.println("║   0. Exit                            ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("Choose: ");
    }

    // ── Patient actions ───────────────────────────────────────────────────────

    private static void addPatient() {
        try {
            System.out.print("ID: ");        int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Name: ");      String name  = sc.nextLine().trim();
            System.out.print("Phone: ");     String phone = sc.nextLine().trim();
            System.out.print("Email: ");     String email = sc.nextLine().trim();
            System.out.print("Blood type: ");String blood = sc.nextLine().trim();
            manager.addPatient(new Patient(id, name, phone, email, blood));
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        } catch (SQLException e) {
            System.out.println("[DB] " + e.getMessage());
        }
    }

    private static void removePatient() {
        try {
            System.out.print("Patient ID to remove: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            manager.removePatient(id);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        } catch (PatientNotFoundException e) {
            System.out.println("[!] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[DB] " + e.getMessage());
        }
    }

    private static void listPatients() {
        List<Patient> list = manager.getAllPatients();
        if (list.isEmpty()) { System.out.println("No patients registered."); return; }
        System.out.println("\n── Patients ─────────────────────────────");
        list.forEach(p -> System.out.println("  " + p.getInfo()));
        System.out.println("──────────────────────────────────────────");
    }

    // ── Doctor actions ────────────────────────────────────────────────────────

    private static void addDoctor() {
        try {
            System.out.print("ID: ");          int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Name: ");        String name      = sc.nextLine().trim();
            System.out.print("Phone: ");       String phone     = sc.nextLine().trim();
            System.out.print("Email: ");       String email     = sc.nextLine().trim();
            System.out.print("Specialty: ");   String specialty = sc.nextLine().trim();
            System.out.print("License ID: "); String license   = sc.nextLine().trim();
            manager.addDoctor(new Doctor(id, name, phone, email, specialty, license));
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        } catch (SQLException e) {
            System.out.println("[DB] " + e.getMessage());
        }
    }

    private static void listDoctors() {
        List<Doctor> list = manager.getAllDoctors();
        if (list.isEmpty()) { System.out.println("No doctors registered."); return; }
        System.out.println("\n── Doctors ──────────────────────────────");
        list.forEach(d -> System.out.println("  " + d.getInfo()));
        System.out.println("──────────────────────────────────────────");
    }

    // ── Appointment actions ───────────────────────────────────────────────────

    private static void bookAppointment() {
        try {
            System.out.print("Patient ID: ");    int pid = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Doctor ID: ");     int did = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Date/time (yyyy-MM-dd HH:mm): ");
            LocalDateTime dt = LocalDateTime.parse(sc.nextLine().trim(), DT_FMT);
            manager.bookAppointment(pid, did, dt);
        } catch (PatientNotFoundException e) {
            System.out.println("[!] " + e.getMessage());
        } catch (AppointmentConflictException e) {
            System.out.println("[!] Conflict: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("[!] Invalid date format. Use yyyy-MM-dd HH:mm");
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        } catch (Exception e) {
            System.out.println("[!] " + e.getMessage());
        }
    }

    private static void cancelAppointment() {
        try {
            System.out.print("Appointment ID to cancel: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            manager.cancelAppointment(id);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        } catch (SQLException e) {
            System.out.println("[DB] " + e.getMessage());
        }
    }

    private static void processNextAppointment() {
        manager.processNextPending();
    }

    private static void viewDoctorAppointments() {
        try {
            System.out.print("Doctor ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            if (manager.getDb() != null)
                manager.getDb().listAppointmentsByDoctor(id);
            else {
                manager.getAppointmentsForDoctor(id).forEach(a -> System.out.println("  " + a));
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        } catch (SQLException e) {
            System.out.println("[DB] " + e.getMessage());
        }
    }

    // ── Medical record actions ────────────────────────────────────────────────

    private static MedicalRecord lastRecord = null;

    private static void recordDiagnosis() {
        try {
            System.out.print("Patient ID: "); int pid = Integer.parseInt(sc.nextLine().trim());
            manager.findPatient(pid); // validates patient exists
            System.out.print("Diagnosis: "); String diag = sc.nextLine().trim();
            lastRecord = new MedicalRecord(pid, diag);

            System.out.print("Add prescription? (y/n): ");
            while (sc.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.print("  Medication: ");  String med  = sc.nextLine().trim();
                System.out.print("  Dosage: ");      String dose = sc.nextLine().trim();
                System.out.print("  Frequency: ");   String freq = sc.nextLine().trim();
                System.out.print("  Days: ");        int days    = Integer.parseInt(sc.nextLine().trim());
                lastRecord.addPrescription(new Prescription(med, dose, freq, days));
                manager.findPatient(pid).receiveTreatment(med + " " + dose);
                System.out.print("Add another? (y/n): ");
            }
            System.out.println(lastRecord);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid input.");
        } catch (PatientNotFoundException e) {
            System.out.println("[!] " + e.getMessage());
        } catch (InvalidRecordException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }

    private static void saveMedicalRecord() {
        if (lastRecord == null) { System.out.println("[!] No record to save. Record a diagnosis first."); return; }
        try {
            Serializer.save(lastRecord);
        } catch (Exception e) {
            System.out.println("[!] Save failed: " + e.getMessage());
        }
    }

    private static void loadMedicalRecord() {
        try {
            System.out.print("Patient ID to load record for: ");
            int pid = Integer.parseInt(sc.nextLine().trim());
            if (!Serializer.recordExists(pid)) {
                System.out.println("[!] No saved record for patient #" + pid);
                return;
            }
            MedicalRecord r = Serializer.load(pid);
            System.out.println(r);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        } catch (Exception e) {
            System.out.println("[!] Load failed: " + e.getMessage());
        }
    }

    // ── File export actions ───────────────────────────────────────────────────

    private static void exportAppointmentsToFile() {
        try {
            String path = FileHandler.exportAppointments(manager.getAllAppointments());
            System.out.println("[✓] Exported to: " + path);
        } catch (Exception e) {
            System.out.println("[!] Export failed: " + e.getMessage());
        }
    }

    private static void exportPatientReport() {
        try {
            System.out.print("Patient ID: ");
            int pid = Integer.parseInt(sc.nextLine().trim());
            Patient p = manager.findPatient(pid);
            String path = FileHandler.exportPatientReport(p, manager.getAppointmentsForPatient(pid));
            System.out.println("[✓] Report saved to: " + path);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid ID.");
        } catch (PatientNotFoundException e) {
            System.out.println("[!] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[!] Export failed: " + e.getMessage());
        }
    }

    // ── Demo seed data ────────────────────────────────────────────────────────

    private static void seedDemoData() {
        try {
            Doctor d1 = new Doctor(1, "Sarah Chen",   "555-1001", "s.chen@hospital.com",   "Cardiology",  "LIC-001");
            Doctor d2 = new Doctor(2, "Omar Farouq",  "555-1002", "o.farouq@hospital.com", "Neurology",   "LIC-002");
            Doctor d3 = new Doctor(3, "Lena Müller",  "555-1003", "l.muller@hospital.com", "Orthopedics", "LIC-003");
            manager.addDoctor(d1); manager.addDoctor(d2); manager.addDoctor(d3);

            Patient p1 = new Patient(101, "Ali Ben Salah",   "555-2001", "ali@email.com",   "A+");
            Patient p2 = new Patient(102, "Maya Trabelsi",   "555-2002", "maya@email.com",  "O-");
            Patient p3 = new Patient(103, "Karim Hammami",   "555-2003", "karim@email.com", "B+");
            manager.addPatient(p1); manager.addPatient(p2); manager.addPatient(p3);

            manager.bookAppointment(101, 1, LocalDateTime.of(2026, 4, 10, 9, 0));
            manager.bookAppointment(102, 2, LocalDateTime.of(2026, 4, 10, 10, 30));
            manager.bookAppointment(103, 1, LocalDateTime.of(2026, 4, 11, 14, 0));

            System.out.println("\n[Demo data loaded — 3 doctors, 3 patients, 3 appointments]\n");
        } catch (Exception e) {
            System.out.println("[Seed] " + e.getMessage());
        }
    }
}
