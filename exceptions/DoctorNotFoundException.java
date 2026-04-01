package exceptions;

/**
 * Thrown when a doctor cannot be found by the given ID.
 */
public class DoctorNotFoundException extends Exception {
    private final String doctorId;

    public DoctorNotFoundException(String doctorId) {
        super("Doctor not found with ID: " + doctorId);
        this.doctorId = doctorId;
    }

    public String getDoctorId() { return doctorId; }
}
