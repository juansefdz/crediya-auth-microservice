package co.com.pragma.usecase.user;

import co.com.pragma.model.auth.Credential;
import co.com.pragma.model.auth.gateways.CredentialGateway;
import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.customExceptions.DocumentAlreadyExistsException;
import co.com.pragma.model.customExceptions.EmailAlreadyExistsException;
import co.com.pragma.model.customExceptions.InvalidSalaryException;
import co.com.pragma.model.customExceptions.RoleNotFoundException;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
@Slf4j
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderGateway passwordEncoder;
    private final CredentialGateway credentialGateWay;

    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    public Mono<User> execute(User userDraft, String roleIdString, String plainPassword) {
        log.info("Iniciando CU crear usuario, email hash={}",
                userDraft.getEmail() != null ? userDraft.getEmail().hashCode() : null);

        Mono<String> hashedPasswordMono = passwordEncoder.encode(plainPassword);

        Mono<Role> roleMono = Mono.fromCallable(() -> Long.parseLong(roleIdString))
                .onErrorMap(NumberFormatException.class,
                        e -> new RoleNotFoundException("El ID de rol '" + roleIdString + "' no es un número válido."))
                .flatMap(roleRepository::findById)
                .switchIfEmpty(Mono.error(new RoleNotFoundException("El rol con ID " + roleIdString + " no existe.")));

        return Mono.zip(roleMono, hashedPasswordMono)
                .flatMap(tuple -> {
                    Role role = tuple.getT1();
                    String hashedPassword = tuple.getT2();

                    // Prepara user con rol (aunque el rol no viva en la tabla, sí en el dominio)
                    User userPrepared = userDraft.withRol(role);

                    return validateAllRules(userPrepared)
                            .flatMap(userRepository::save)
                            .flatMap(savedUser -> {
                                // Guarda credencial usando el hash directamente del encoder
                                Credential credentialToSave = Credential.builder()
                                        .usuarioId(savedUser.getId())
                                        .email(savedUser.getEmail())
                                        .passwordHash(hashedPassword)
                                        .enabled(true)
                                        .build();

                                return credentialGateWay.save(credentialToSave)
                                        // Reatacha el rol ANTES de devolver
                                        .thenReturn(savedUser.withRol(role));
                            });
                })
                .doOnSuccess(u -> {
                    String rolNombre = (u.getRol() != null && u.getRol().getNombre() != null)
                            ? u.getRol().getNombre() : "N/A";
                    log.info("Usuario y credencial creados ok, id={}, rol={}", u.getId(), rolNombre);
                })
                .doOnError(e -> log.error("Error creando usuario: {}", e.getMessage()));
    }

    private Mono<User> validateAllRules(User user) {
        if (user.getSalarioBase().compareTo(BigDecimal.ZERO) < 0
                || user.getSalarioBase().compareTo(MAX_SALARY) > 0) {
            return Mono.error(new InvalidSalaryException(user.getSalarioBase().toPlainString()));
        }
        Mono<Boolean> emailExists = userRepository.existsByEmail(user.getEmail());
        Mono<Boolean> documentExists = userRepository.existsByDocumentoIdentidad(user.getDocumentoIdentidad());

        return Mono.zip(emailExists, documentExists)
                .flatMap(tuple -> {
                    boolean emailTaken = tuple.getT1();
                    boolean documentTaken = tuple.getT2();

                    if (emailTaken) {
                        return Mono.error(new EmailAlreadyExistsException("El email ya está registrado "));
                    }
                    if (documentTaken) {
                        return Mono.error(new DocumentAlreadyExistsException(user.getDocumentoIdentidad()));
                    }
                    return Mono.just(user);
                });
    }
}
