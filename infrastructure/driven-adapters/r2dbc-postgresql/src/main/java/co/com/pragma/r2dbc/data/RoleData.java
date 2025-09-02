package co.com.pragma.r2dbc.data;


import org.springframework.data.relational.core.mapping.Table;

import org.springframework.data.annotation.Id;
import lombok.Data;

@Data
@Table("roles")
public class RoleData {

    @Id
    private Long id;
    private String nombre;
    private String descripcion;

}
