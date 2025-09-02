package co.com.pragma.r2dbc.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Table("usuarios")
public class UserData {

    @Id
    private Long id;
    private String nombre;
    private String apellidos;
    private String documentoIdentidad;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String email;
    private String telefono;
    private BigDecimal salarioBase;

    @Column("rol_id")
    private Long idRol;
}