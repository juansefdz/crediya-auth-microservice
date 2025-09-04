package co.com.pragma.model.customExceptions;

public class InvalidSalaryException extends BusinessException {

    private static final String ERROR_CODE = "ERROR03";
    private static final String ERROR_MESSAGE = "El salario ingresado no es valido";

    public InvalidSalaryException(String message) {

        super (ERROR_CODE,ERROR_MESSAGE);

        //llamado al constructor padre
    }
}