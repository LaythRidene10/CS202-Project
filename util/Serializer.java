package util;

import model.MedicalRecord;

import java.io.*;
import java.nio.file.*;

public class Serializer {

    private static final String DIR = "records/";

    static {
        try { Files.createDirectories(Paths.get(DIR)); }
        catch (IOException e) { System.err.println("Could not create records/ dir: " + e.getMessage()); }
    }

    public static void save(MedicalRecord record) throws IOException {
        String path = DIR + record.getPatientId() + ".ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(record);
            System.out.println("[Serializer] Record saved to " + path);
        }
    }

    public static MedicalRecord load(int patientId) throws IOException, ClassNotFoundException {
        String path = DIR + patientId + ".ser";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            MedicalRecord r = (MedicalRecord) ois.readObject();
            System.out.println("[Serializer] Record loaded for patient #" + patientId);
            return r;
        }
    }

    public static boolean recordExists(int patientId) {
        return new File(DIR + patientId + ".ser").exists();
    }
}
