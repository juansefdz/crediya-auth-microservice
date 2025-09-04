package co.com.pragma.model.customExceptions;

// En el paquete de customExceptions
public class RoleNotFoundException extends BusinessException {
    private static final String ERROR_CODE = "ERROR06";
    private static final String ERROR_MESSAGE = "El rol con id '%s' no existe.";

    public RoleNotFoundException(String roleId) {
        super(ERROR_CODE, String.format(ERROR_MESSAGE, roleId));
    }
}