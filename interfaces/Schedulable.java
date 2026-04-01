package interfaces;

import java.time.LocalDateTime;

public interface Schedulable {
    boolean isAvailable(LocalDateTime dateTime);
    void addAppointmentSlot(LocalDateTime dateTime);
}
