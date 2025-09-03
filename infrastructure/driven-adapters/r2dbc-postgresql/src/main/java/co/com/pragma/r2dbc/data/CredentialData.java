package co.com.pragma.r2dbc.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
@Data
@Table("credentials")
public class CredentialData {
    @Id
    private Long id;
    private String email;
    private String passwordHash;
    private Boolean enabled;
    @Column("usuario_id")
    private Long userId;
}