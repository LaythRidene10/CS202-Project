# Hospital Management System
### Java Course Project — covers 30% of curriculum

---

## Requirements Coverage

| Requirement              | Implementation                                                    |
|--------------------------|-------------------------------------------------------------------|
| Abstract class           | `model/Person.java` — abstract `getRole()` and `getInfo()`       |
| Interfaces               | `interfaces/Treatable.java`, `interfaces/Schedulable.java`        |
| Custom exceptions        | `PatientNotFoundException`, `AppointmentConflictException`, `InvalidRecordException` |
| File handling            | `util/FileHandler.java` — exports `.txt` reports to `exports/`   |
| Collections & Generics   | `manager/HospitalManager.java` — `List<Patient>`, `Map<Integer, List<Appointment>>`, `Queue<Appointment>` |
| Serialization            | `util/Serializer.java` — saves/loads `MedicalRecord` as `.ser`   |
| Database (JDBC)          | `db/HospitalDB.java` — MySQL CRUD for patients, doctors, appointments |

---

## Project Structure

```
hospital/
├── interfaces/
│   ├── Treatable.java          # Patient contract: receiveTreatment(), getMedicalHistory()
│   └── Schedulable.java        # Doctor contract: isAvailable(), addAppointmentSlot()
├── model/
│   ├── Person.java             # Abstract base class (id, name, phone, email)
│   ├── Patient.java            # Extends Person, implements Treatable
│   ├── Doctor.java             # Extends Person, implements Schedulable
│   ├── Appointment.java        # Links Patient + Doctor with datetime + status
│   ├── MedicalRecord.java      # Implements Serializable, holds diagnosis + prescriptions
│   └── Prescription.java       # Implements Serializable (medication, dosage, duration)
├── exceptions/
│   ├── PatientNotFoundException.java
│   ├── AppointmentConflictException.java
│   └── InvalidRecordException.java
├── db/
│   └── HospitalDB.java         # JDBC — MySQL connection, auto-creates tables, full CRUD
├── manager/
│   └── HospitalManager.java    # Business logic + Collections usage
├── util/
│   ├── Serializer.java         # ObjectOutputStream / ObjectInputStream
│   └── FileHandler.java        # BufferedWriter file exports
└── Main.java                   # Console menu (14 options)
```

---

## Setup & Run

### 1. MySQL setup
```sql
CREATE DATABASE hospital_db;
```
Update `db/HospitalDB.java` with your MySQL password (line: `PASS = "password"`).

### 2. Download MySQL JDBC driver
Get `mysql-connector-j-x.x.x.jar` from https://dev.mysql.com/downloads/connector/j/

### 3. Compile
```bash
javac -cp ".;mysql-connector-j-x.x.x.jar" -d out \
  interfaces/*.java \
  exceptions/*.java \
  model/*.java \
  db/*.java \
  util/*.java \
  manager/*.java \
  Main.java
```

On Linux/Mac use `:` instead of `;`:
```bash
javac -cp ".:mysql-connector-j-x.x.x.jar" -d out \
  interfaces/*.java exceptions/*.java model/*.java \
  db/*.java util/*.java manager/*.java Main.java
```

### 4. Run
```bash
java -cp "out;mysql-connector-j-x.x.x.jar" Main
# Linux/Mac:
java -cp "out:mysql-connector-j-x.x.x.jar" Main
```

---

## Menu Options

```
1.  Add patient
2.  Remove patient                  → throws PatientNotFoundException
3.  List all patients
4.  Add doctor
5.  List all doctors
6.  Book appointment                → throws PatientNotFoundException, AppointmentConflictException
7.  Cancel appointment
8.  Process next pending            → uses Queue<Appointment>
9.  View doctor's appointments (DB) → JDBC query with JOIN
10. Record diagnosis + prescription → throws InvalidRecordException
11. Save medical record             → Serialization (ObjectOutputStream)
12. Load medical record             → Deserialization (ObjectInputStream)
13. Export appointments to .txt     → File handling (BufferedWriter)
14. Export patient report to .txt   → File handling (BufferedWriter)
```

---

## Notes
- The app seeds **3 doctors, 3 patients, 3 appointments** on startup so you can test immediately.
- If MySQL is unavailable, the app runs in **offline mode** (in-memory only, no DB persistence).
- Serialized records are saved in `records/` folder.
- Exported `.txt` files are saved in `exports/` folder.
