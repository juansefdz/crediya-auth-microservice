package co.com.pragma.model.customExceptions;



public class DocumentAlreadyExistsException extends BusinessException {

    private static final String ERROR_CODE = "ERROR02";
    private static final String ERROR_MESSAGE = "El documento de identidad ya se encuentra registrado.";

    public DocumentAlreadyExistsException(String message) {
        super(ERROR_CODE, ERROR_MESSAGE);

        //llamado al constructor padre
    }
}