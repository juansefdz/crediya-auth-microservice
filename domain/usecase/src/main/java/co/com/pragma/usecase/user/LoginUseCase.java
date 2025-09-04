package co.com.pragma.usecase.user;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.auth.gateways.PermissionGateway;
import co.com.pragma.usecase.auth.AuthenticateUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionGateway permissionRepository;
    private final PasswordEncoderGateway passwordEncoder;

    public Mono<AuthenticatedUser> execute(String emailOrUsername, String rawPassword) {
        final String normalized = normalize(emailOrUsername);

        return userRepository.findByEmail(normalized)
                .switchIfEmpty(Mono.error(new AuthException("Credenciales inválidas")))
                .flatMap(user -> validatePassword(rawPassword, user))
                .flatMap(u -> roleRepository.getRolesByUserId(u.getId()).collectList()
                        .flatMap(roles -> loadPermissions(roles)
                                .map(perms -> new AuthenticatedUser(u, roles, perms))));
    }

    private Mono<User> validatePassword(String raw, User user) {
        if (Boolean.FALSE.equals(user.getEnabled())) {
            return Mono.error(new AuthenticateUserUseCase.UserDisabledException());
        }

        return passwordEncoder.matches(raw, user.getPasswordHash())
                .filter(isValid -> isValid) // Deja pasar el flujo solo si el valor es 'true'
                .map(isValid -> user) // Transforma el 'true' en el objeto 'user'
                .switchIfEmpty(Mono.error(new AuthenticateUserUseCase.InvalidCredentialsException())); // Si el flujo se vació (porque era 'false'), lanza el error.
    }

    private Mono<List<String>> loadPermissions(List<Role> roles) {
        return Flux.fromIterable(roles)
                .map(Role::getId)
                .flatMap(permissionRepository::getPermissionsByRoleId)
                .distinct()
                .collectList();
    }

    private String normalize(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    @Value
    public static class AuthenticatedUser {
        User user;
        List<Role> roles;
        List<String> permissions;
    }

    public static class AuthException extends RuntimeException {
        public AuthException(String message) { super(message); }
    }
}
