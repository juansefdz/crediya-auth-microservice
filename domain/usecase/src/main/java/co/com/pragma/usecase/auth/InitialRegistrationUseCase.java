package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.Credential;
import co.com.pragma.model.auth.gateways.CredentialGateway;
import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.customExceptions.EmailAlreadyExistsException;
import co.com.pragma.model.customExceptions.RoleNotFoundException;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class InitialRegistrationUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderGateway passwordEncoder;
    private final CredentialGateway credentialGateway;

    private final Long adminRoleId;
    private final Long defaultRoleId;

    public Mono<User> execute(String emailRaw, String passwordRaw) {
        final String email = normalize(emailRaw);

        log.info("InitialRegistrationUseCase - intento de registro email={}", email);

        return ensureEmailAvailable(email)                 // Mono<Void>
                .then(chooseRoleId())                      // <— CLAVE: usar then(...) en vez de flatMap
                .flatMap(this::loadRoleOrFail)
                .flatMap(role -> createAndSaveUser(email, role, passwordRaw))
                .switchIfEmpty(Mono.error(new IllegalStateException("Flujo vacío al registrar usuario")))
                .doOnNext(u -> log.info("Registro inicial OK email={}, userId={}", email, u.getId()))
                .doOnError(e -> log.warn("Registro inicial FAIL email={}, cause={}", email, e.getMessage()));
    }

    private Mono<Void> ensureEmailAvailable(String email) {
        return credentialGateway.existsByEmail(email)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailAlreadyExistsException("El email ya está registrado: " + email));
                    }
                    // correo disponible → no emitimos valor, solo completamos
                    return Mono.empty();
                });
    }

    /** Si no hay usuarios en el sistema -> adminRoleId, de lo contrario defaultRoleId. */
    private Mono<Long> chooseRoleId() {
        return userRepository.count()
                .map(count -> count == 0 ? adminRoleId : defaultRoleId);
    }

    private Mono<Role> loadRoleOrFail(Long roleId) {
        return roleRepository.findById(roleId) // ajusta a String si tu repo usa String
                .switchIfEmpty(Mono.error(new RoleNotFoundException("No existe el rol con id: " + roleId)));
    }

    private Mono<User> createAndSaveUser(String email, Role role, String passwordRaw) {
        User draft = User.builder()
                .email(email)
                .rol(role)
                .enabled(true) // si tu modelo lo maneja
                .build();

        return userRepository.save(draft)
                .flatMap(savedUser ->
                        passwordEncoder.encode(passwordRaw)
                                .map(hash -> Credential.builder()
                                        .usuarioId(savedUser.getId()) // o userId, según tu modelo
                                        .email(email)
                                        .passwordHash(hash)
                                        .enabled(true)
                                        .build())
                                .flatMap(credentialGateway::save)
                                .thenReturn(savedUser)
                );
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
