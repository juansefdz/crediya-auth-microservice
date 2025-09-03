package co.com.pragma.model.auth;

import lombok.Value;
import lombok.Builder;
@Value
@Builder
public class LoginCommand {
    String email;
    String password;
}