package co.com.pragma.usecase.user;

import co.com.pragma.model.auth.gateways.CredentialGateway;
import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.customExceptions.DocumentAlreadyExistsException;
import co.com.pragma.model.customExceptions.EmailAlreadyExistsException;
import co.com.pragma.model.customExceptions.InvalidDataException;
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
    private final CredentialGateway credentialGateway;

    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");

    public Mono<User> execute(User userInput, String roleIdString, String plainPassword) {
        final String email = normalize(userInput.getEmail());
        log.info("Iniciando CU crear/actualizar usuario, email hash={}", email != null ? email.hashCode() : null);

        if (userInput.getSalarioBase() == null
                || userInput.getSalarioBase().compareTo(BigDecimal.ZERO) < 0
                || userInput.getSalarioBase().compareTo(MAX_SALARY) > 0) {
            return Mono.error(new InvalidSalaryException(
                    userInput.getSalarioBase() == null ? "null" : userInput.getSalarioBase().toPlainString()));
        }

        Mono<Role> roleMono = Mono.fromCallable(() -> Long.parseLong(roleIdString))
                .onErrorMap(NumberFormatException.class,
                        e -> new RoleNotFoundException("El ID de rol '" + roleIdString + "' no es un número válido."))
                .flatMap(roleRepository::findById)
                .switchIfEmpty(Mono.error(new RoleNotFoundException("El rol con ID " + roleIdString + " no existe.")));

        final boolean hasPasswordOuter = plainPassword != null && !plainPassword.isBlank();
        Mono<String> hashMono = hasPasswordOuter
                ? passwordEncoder.encode(plainPassword)
                : Mono.justOrEmpty((String) null);

        return Mono.zip(roleMono, hashMono.switchIfEmpty(Mono.just("")))
                .flatMap(tuple -> {
                    Role role = tuple.getT1();
                    String hashOrEmpty = tuple.getT2();
                    final boolean hasPassword = !hashOrEmpty.isBlank();

                    return userRepository.findByEmail(email)
                            .doOnNext(u -> log.info(
                                    "Encontrado en BD: id={}, email={}, enabled={}, nombre='{}', doc='{}'",
                                    u.getId(), u.getEmail(), u.getEnabled(), u.getNombre(), u.getDocumentoIdentidad()
                            ))
                            .flatMap(existing -> {
                                log.info("Validando usuario existente en execute()");

                                if (!isDraft(existing)) {
                                    return Mono.error(new EmailAlreadyExistsException("El email ya está registrado"));
                                }

                                return validarDocumentoExcluyendo(existing.getId(), userInput.getDocumentoIdentidad())
                                        .then(Mono.defer(() -> {
                                            User merged = existing.toBuilder()
                                                    .nombre(userInput.getNombre())
                                                    .apellidos(userInput.getApellidos())
                                                    .documentoIdentidad(userInput.getDocumentoIdentidad())
                                                    .fechaNacimiento(userInput.getFechaNacimiento())
                                                    .direccion(userInput.getDireccion())
                                                    .telefono(userInput.getTelefono())
                                                    .salarioBase(userInput.getSalarioBase())
                                                    .enabled(true)
                                                    .rol(role)
                                                    .passwordHash(hasPassword ? hashOrEmpty : existing.getPasswordHash())
                                                    .build();

                                            return userRepository.save(merged)
                                                    .switchIfEmpty(Mono.error(new InvalidDataException("No se pudo actualizar usuario con email=" + email)))
                                                    .flatMap(saved -> {
                                                        if (hasPassword) {
                                                            return credentialGateway.upsert(saved.getEmail(), hashOrEmpty, saved.getId())
                                                                    .thenReturn(saved);
                                                        }
                                                        return Mono.just(saved);
                                                    });
                                        }));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                if (hasPasswordOuter && plainPassword.length() < 8) {
                                    return Mono.error(new InvalidDataException("La contraseña debe tener al menos 8 caracteres"));
                                }
                                return validarDocumentoNuevo(userInput.getDocumentoIdentidad())
                                        .then(Mono.defer(() -> {
                                            User toCreate = userInput.toBuilder()
                                                    .email(email)
                                                    .enabled(true)
                                                    .rol(role)
                                                    .passwordHash(hashOrEmpty)
                                                    .build();

                                            return userRepository.save(toCreate)
                                                    .switchIfEmpty(Mono.error(new InvalidDataException("No se pudo crear usuario con email=" + email)))
                                                    .flatMap(saved ->
                                                            credentialGateway.upsert(saved.getEmail(), hashOrEmpty, saved.getId())
                                                                    .thenReturn(saved));
                                        }));
                            }));
                })
                .doOnSuccess(u -> {
                    if (u != null) {
                        log.info("Usuario OK id={}, rol={}",
                                u.getId(),
                                (u.getRol() != null && u.getRol().getNombre() != null) ? u.getRol().getNombre() : "N/A");
                    }
                })
                .doOnError(e -> log.error("Error creando/actualizando usuario: {}", e.getMessage()));
    }

    private Mono<Void> validarDocumentoNuevo(String doc) {
        if (doc == null) return Mono.error(new InvalidDataException("El documento no puede ser nulo"));
        return userRepository.existsByDocumentoIdentidad(doc)
                .flatMap(exists -> exists
                        ? Mono.<Void>error(new DocumentAlreadyExistsException(doc))
                        : Mono.empty());
    }

    private Mono<Void> validarDocumentoExcluyendo(Long excludeId, String doc) {
        if (doc == null) return Mono.error(new InvalidDataException("El documento no puede ser nulo"));
        return userRepository.existsByDocumentoIdentidadExcludingId(doc, excludeId)
                .flatMap(exists -> exists
                        ? Mono.<Void>error(new DocumentAlreadyExistsException(doc))
                        : Mono.empty());
    }

    private String normalize(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    private boolean isDraft(User u) {
        if (u.getEnabled() == null || u.getEnabled()) {
            return false;
        }
        boolean sinNombre = (u.getNombre() == null || u.getNombre().isBlank());
        boolean sinDoc = (u.getDocumentoIdentidad() == null || u.getDocumentoIdentidad().isBlank());
        return sinNombre && sinDoc;
    }
}