package co.com.pragma.api.dto;

import co.com.pragma.api.validation.DateFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    @Schema(example = "Juan")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @Schema(example = "Pérez Gómez")
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;
    @Schema(example = "123456789")
    @NotBlank(message = "El documento de identidad es obligatorio")
    @Size(min = 5, max = 20, message = "El documento debe tener entre 5 y 20 caracteres")
    private String documentoIdentidad;
    @Schema(example = "1993-05-14")
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @DateFormat
    private LocalDate fechaNacimiento;
    @Schema(example = "Calle 45 #23-10, Medellín")
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    @Schema(example = "juan.perez@example.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
    @Schema(example = "Contraseña123!", description = "Contraseña del usuario")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    @Schema(example = "+57 3101234567")
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    @Schema(example = "3500000", minimum = "0", maximum = "15000000")
    @NotNull(message = "El salario base es obligatorio.")
    @DecimalMin(value = "0.0", message = "El salario no puede ser negativo.")
    @DecimalMax(value = "15000000.0", message = "El salario no puede exceder 15,000,000.")
    private BigDecimal salarioBase;
    @Schema(example = "2", description = "Id del rol asignado al usuario")
    @NotNull(message = "El ID del rol no puede ser nulo")
    private Long idRol;
}
