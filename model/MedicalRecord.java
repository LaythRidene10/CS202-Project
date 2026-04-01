package model;

import exceptions.InvalidRecordException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int patientId;
    private String diagnosis;
    private LocalDate recordDate;
    private final List<Prescription> prescriptions = new ArrayList<>();

    public MedicalRecord(int patientId, String diagnosis) throws InvalidRecordException {
        if (diagnosis == null || diagnosis.isBlank())
            throw new InvalidRecordException("Diagnosis cannot be empty.");
        this.patientId  = patientId;
        this.diagnosis  = diagnosis;
        this.recordDate = LocalDate.now();
    }

    public void addPrescription(Prescription p) {
        prescriptions.add(p);
    }

    public int getPatientId()              { return patientId; }
    public String getDiagnosis()           { return diagnosis; }
    public LocalDate getRecordDate()       { return recordDate; }
    public List<Prescription> getPrescriptions() { return prescriptions; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Medical Record ===\n")
          .append("Patient ID : ").append(patientId).append("\n")
          .append("Date       : ").append(recordDate).append("\n")
          .append("Diagnosis  : ").append(diagnosis).append("\n")
          .append("Prescriptions:\n");
        if (prescriptions.isEmpty()) sb.append("  None\n");
        else prescriptions.forEach(p -> sb.append("  - ").append(p).append("\n"));
        return sb.toString();
    }
}
