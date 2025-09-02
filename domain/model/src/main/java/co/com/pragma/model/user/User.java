package co.com.pragma.model.user;

import java.math.BigDecimal;
import java.time.LocalDate;

import co.com.pragma.model.role.Role;
import lombok.*;

@Value
@Builder(toBuilder = true)
public class User {

    Long id;
    String nombre;
    String apellidos;
    String documentoIdentidad;
    LocalDate fechaNacimiento;
    String direccion;
    String email;
    String telefono;
    BigDecimal salarioBase;

    //jwt
    String passwordHash;
    Boolean enabled;

    @With
    Role rol;
}
