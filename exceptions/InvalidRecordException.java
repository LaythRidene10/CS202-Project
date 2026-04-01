package exceptions;

public class InvalidRecordException extends Exception {
    public InvalidRecordException(String message) {
        super("Invalid medical record: " + message);
    }
}
