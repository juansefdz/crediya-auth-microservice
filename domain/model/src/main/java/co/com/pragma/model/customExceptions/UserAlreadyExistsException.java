package co.com.pragma.model.customExceptions;

public class UserAlreadyExistsException extends BusinessException {

    private static final String ERROR_CODE = "ERROR05";
    private static final String ERROR_MESSAGE = "El usuario ya se encuentra registrado";

    public UserAlreadyExistsException(){
        super (ERROR_CODE,ERROR_MESSAGE);
    }

}
