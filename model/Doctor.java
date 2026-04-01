package model;

import interfaces.Schedulable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends Person implements Schedulable {
    private String specialty;
    private String licenseId;
    private final List<LocalDateTime> availableSlots = new ArrayList<>();

    public Doctor(int id, String name, String phone, String email, String specialty, String licenseId) {
        super(id, name, phone, email);
        this.specialty = specialty;
        this.licenseId = licenseId;
    }

    @Override public String getRole() { return "Doctor"; }

    @Override
    public String getInfo() {
        return toString() + " | Specialty: " + specialty + " | License: " + licenseId;
    }

    @Override
    public boolean isAvailable(LocalDateTime dateTime) {
        return availableSlots.contains(dateTime);
    }

    @Override
    public void addAppointmentSlot(LocalDateTime dateTime) {
        availableSlots.add(dateTime);
    }

    public void removeSlot(LocalDateTime dateTime) {
        availableSlots.remove(dateTime);
    }

    public String getSpecialty()  { return specialty; }
    public String getLicenseId()  { return licenseId; }
    public List<LocalDateTime> getAvailableSlots() { return availableSlots; }
}
