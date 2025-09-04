package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.gateways.CredentialGateway;
import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.customExceptions.EmailAlreadyExistsException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class InitialRegistrationUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoder;
    private final CredentialGateway credentialGateway;

    public Mono<User> execute(String emailRaw, String passwordRaw) {
        final String email = normalize(emailRaw);
        log.info("InitialRegistrationUseCase - intento de registro email={}", email);

        return userRepository.findByEmail(email)
                .flatMap(existing -> {
                    if (isDraft(existing)) {
                        // Reusar borrador: solo asegurar credencial con el usuario_id del borrador
                        return passwordEncoder.encode(passwordRaw)
                                .flatMap(hash -> credentialGateway.upsert(existing.getEmail(), hash, existing.getId()))
                                .thenReturn(existing)
                                .doOnSuccess(u -> log.info("Registro inicial OK (reused draft) email={}, userId={}", email, u.getId()));
                    }
                    // Existe activo → no permitir registro inicial
                    return Mono.error(new EmailAlreadyExistsException("El correo electrónico ya se encuentra registrado."));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // No existe → crear borrador
                    User draft = User.builder()
                            .email(email)
                            .enabled(false)   // clave para detectar borrador
                            .build();

                    return userRepository.save(draft)
                            .flatMap(saved ->
                                    passwordEncoder.encode(passwordRaw)
                                            .flatMap(hash -> credentialGateway.upsert(saved.getEmail(), hash, saved.getId()))
                                            .thenReturn(saved)
                            )
                            .doOnSuccess(u -> log.info("Registro inicial OK email={}, userId={}", email, u.getId()));
                }))
                .doOnError(e -> log.warn("Registro inicial FAIL email={}, cause={}", email, e.getMessage()));
    }


    private String normalize(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }


    private boolean isDraft(User u) {
        return Boolean.FALSE.equals(u.getEnabled())
                && u.getNombre() == null
                && u.getDocumentoIdentidad() == null;
    }
}
