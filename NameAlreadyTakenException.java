public class NameAlreadyTakenException extends Exception {
    public NameAlreadyTakenException() {
    }

    public NameAlreadyTakenException(String message) {
        super(message);
    }
}
