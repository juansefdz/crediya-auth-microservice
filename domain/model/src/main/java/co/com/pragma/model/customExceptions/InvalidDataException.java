package co.com.pragma.model.customExceptions;

public class InvalidDataException extends BusinessException {

    private static final String ERROR_CODE = "ERROR04";

    public InvalidDataException(String message) {
        super(ERROR_CODE, message);
    }
}