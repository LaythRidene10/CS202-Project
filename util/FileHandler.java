package util;

import model.Appointment;
import model.Patient;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileHandler {

    private static final String DIR = "exports/";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    static {
        try { Files.createDirectories(Paths.get(DIR)); }
        catch (IOException e) { System.err.println("Could not create exports/ dir: " + e.getMessage()); }
    }

    public static String exportAppointments(List<Appointment> appointments) throws IOException {
        String filename = DIR + "appointments_" + LocalDateTime.now().format(FMT) + ".txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("=== Appointment Report — " + LocalDateTime.now() + " ===\n\n");
            if (appointments.isEmpty()) {
                bw.write("No appointments found.\n");
            } else {
                for (Appointment a : appointments) {
                    bw.write(a.toString());
                    bw.newLine();
                }
            }
            bw.write("\nTotal: " + appointments.size() + " appointment(s)\n");
        }
        System.out.println("[FileHandler] Exported to " + filename);
        return filename;
    }

    public static String exportPatientReport(Patient p, List<Appointment> history) throws IOException {
        String filename = DIR + "patient_" + p.getId() + "_report.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("=== Patient Report ===\n");
            bw.write(p.getInfo() + "\n\n");
            bw.write("--- Medical History ---\n");
            bw.write(p.getMedicalHistory() + "\n");
            bw.write("--- Appointment History ---\n");
            if (history.isEmpty()) {
                bw.write("No appointments.\n");
            } else {
                for (Appointment a : history) { bw.write(a.toString()); bw.newLine(); }
            }
        }
        System.out.println("[FileHandler] Patient report saved to " + filename);
        return filename;
    }
}
