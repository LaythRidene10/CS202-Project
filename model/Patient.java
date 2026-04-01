package model;

import interfaces.Treatable;
import java.util.ArrayList;
import java.util.List;

public class Patient extends Person implements Treatable {
    private String bloodType;
    private final List<String> medicalHistory = new ArrayList<>();

    public Patient(int id, String name, String phone, String email, String bloodType) {
        super(id, name, phone, email);
        this.bloodType = bloodType;
    }

    @Override public String getRole() { return "Patient"; }

    @Override
    public String getInfo() {
        return toString() + " | Blood: " + bloodType + " | History entries: " + medicalHistory.size();
    }

    @Override
    public void receiveTreatment(String treatment) {
        medicalHistory.add(treatment);
        System.out.println("Treatment recorded for " + name + ": " + treatment);
    }

    @Override
    public String getMedicalHistory() {
        if (medicalHistory.isEmpty()) return "No history.";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < medicalHistory.size(); i++)
            sb.append((i + 1)).append(". ").append(medicalHistory.get(i)).append("\n");
        return sb.toString();
    }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
}
