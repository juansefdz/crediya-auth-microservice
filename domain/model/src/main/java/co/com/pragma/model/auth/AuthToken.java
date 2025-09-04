package co.com.pragma.model.auth;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthToken {
    String token;         // JWT de acceso
    String tokenType;     // "Bearer"
    long   expiresIn;
}