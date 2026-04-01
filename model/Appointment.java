package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    public enum Status { SCHEDULED, COMPLETED, CANCELLED }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private int id;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private Status status;
    private String notes;

    public Appointment(int id, Patient patient, Doctor doctor, LocalDateTime dateTime) {
        this.id       = id;
        this.patient  = patient;
        this.doctor   = doctor;
        this.dateTime = dateTime;
        this.status   = Status.SCHEDULED;
        this.notes    = "";
    }

    public int getId()             { return id; }
    public Patient getPatient()    { return patient; }
    public Doctor getDoctor()      { return doctor; }
    public LocalDateTime getDateTime() { return dateTime; }
    public Status getStatus()      { return status; }
    public String getNotes()       { return notes; }

    public void setStatus(Status status)  { this.status = status; }
    public void setNotes(String notes)    { this.notes  = notes; }

    @Override
    public String toString() {
        return "Appt #" + id + " | " + patient.getName() + " -> Dr. " + doctor.getName()
             + " | " + dateTime.format(FMT) + " | " + status
             + (notes.isBlank() ? "" : " | " + notes);
    }
}
