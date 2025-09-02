package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.AuthToken;
import co.com.pragma.model.auth.Credentials;
import co.com.pragma.model.auth.gateways.AuthGateway;
import co.com.pragma.model.user.User;
import co.com.pragma.model.auth.gateways.JwtProviderGateway;
import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AuthenticateUserUseCase {

    private final AuthGateway authGateway;
    private final PasswordEncoderGateway passwordEncoder;
    private final JwtProviderGateway jwtProvider;

    public Mono<AuthToken> execute(Credentials credentials) {
        final String normalizedEmail = normalize(credentials.email());

        log.info("AuthUseCase - intento de login email={}", normalizedEmail);

        return authGateway.findByEmail(normalizedEmail)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(user -> validatePassword(credentials.password(), user)
                        .flatMap(u -> jwtProvider.generateAccessToken(u)
                                .map(token -> AuthToken.builder()
                                        .token(token)
                                        .tokenType("Bearer")
                                        .expiresIn(jwtProvider.getExpirationSeconds())
                                        .build()
                                )
                        )
                )
                .doOnSuccess(t -> log.info("AuthUseCase - login OK email={}", normalizedEmail))
                .doOnError(e -> log.warn("AuthUseCase - login FAIL email={}, cause={}",
                        normalizedEmail, e.getMessage()));
    }

    private Mono<User> validatePassword(String raw, User user) {
        if (Boolean.FALSE.equals(user.getEnabled())) {
            return Mono.error(new UserDisabledException());
        }

        return passwordEncoder.matches(raw, user.getPasswordHash())
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return Mono.just(user);
                    } else {
                        return Mono.error(new InvalidCredentialsException());
                    }
                });
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }


    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() { super("Usuario o contraseña inválidos"); }
    }
    public static class UserDisabledException extends RuntimeException {
        public UserDisabledException() { super("Usuario deshabilitado"); }
    }
}
