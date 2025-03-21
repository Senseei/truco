package exceptions;

public class RoundTieException extends RuntimeException {
    public RoundTieException(String message) {
        super(message);
    }
}
