package exception;

public class ReadRequestException extends RuntimeException {

    public ReadRequestException(String message) {
        super(message);
    }
}
