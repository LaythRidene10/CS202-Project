package model;

import java.io.Serializable;

public class Prescription implements Serializable {
    private static final long serialVersionUID = 1L;

    private String medication;
    private String dosage;
    private String frequency;
    private int durationDays;

    public Prescription(String medication, String dosage, String frequency, int durationDays) {
        this.medication   = medication;
        this.dosage       = dosage;
        this.frequency    = frequency;
        this.durationDays = durationDays;
    }

    public String getMedication()  { return medication; }
    public String getDosage()      { return dosage; }
    public String getFrequency()   { return frequency; }
    public int getDurationDays()   { return durationDays; }

    @Override
    public String toString() {
        return medication + " | " + dosage + " | " + frequency + " | " + durationDays + " days";
    }
}
