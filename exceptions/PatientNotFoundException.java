package exceptions;

public class PatientNotFoundException extends Exception {
    private final int patientId;

    public PatientNotFoundException(int patientId) {
        super("Patient with ID " + patientId + " was not found.");
        this.patientId = patientId;
    }

    public int getPatientId() { return patientId; }
}
