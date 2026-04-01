package exceptions;

import java.time.LocalDateTime;

public class AppointmentConflictException extends Exception {
    private final LocalDateTime conflictTime;

    public AppointmentConflictException(LocalDateTime conflictTime) {
        super("Appointment conflict at: " + conflictTime);
        this.conflictTime = conflictTime;
    }

    public LocalDateTime getConflictTime() { return conflictTime; }
}
