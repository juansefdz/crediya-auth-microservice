package co.com.pragma.model.customExceptions;

public class EmailAlreadyExistsException extends BusinessException {

    private static final String ERROR_CODE = "ERROR01";
    private static final String ERROR_MESSAGE = "El correo electrónico ya se encuentra registrado.";

    public EmailAlreadyExistsException(String s) {
        super(ERROR_CODE, ERROR_MESSAGE);

        //llamado al constructor padre
    }
}