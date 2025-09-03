package co.com.pragma.model.auth;

import lombok.*;

@Value
@Builder
public class Credential {
    Long usuarioId;
    String email;
    String passwordHash;
    Boolean enabled;
}