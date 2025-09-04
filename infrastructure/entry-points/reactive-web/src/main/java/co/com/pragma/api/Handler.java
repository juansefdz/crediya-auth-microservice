package co.com.pragma.api;

import co.com.pragma.api.dto.LoginRequestDTO;
import co.com.pragma.api.dto.RegistrationRequestDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.mapper.UserApiMapper;
import co.com.pragma.model.customExceptions.EmailAlreadyExistsException;
import co.com.pragma.model.customExceptions.InvalidDataException;
import co.com.pragma.model.customExceptions.RoleNotFoundException;
import co.com.pragma.usecase.auth.AuthenticateUserUseCase;
import co.com.pragma.usecase.auth.InitialRegistrationUseCase;
import co.com.pragma.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final UserUseCase userUseCase;
    private final UserApiMapper userApiMapper;
    private final Validator validator;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final InitialRegistrationUseCase initialRegistrationUseCase;


    public Mono<ServerResponse> registroInicial(ServerRequest request) {
        return request.bodyToMono(RegistrationRequestDTO.class)
                .flatMap(dto -> {

                    if (dto.email() == null || dto.email().isBlank()) {
                        return Mono.error(new InvalidDataException("email requerido"));
                    }
                    if (dto.password() == null || dto.password().isBlank()) {
                        return Mono.error(new InvalidDataException("password requerido"));
                    }
                    return initialRegistrationUseCase.execute(dto.email().trim(), dto.password());
                })
                .flatMap(user ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userApiMapper.toDTO(user))
                )
                .onErrorResume(EmailAlreadyExistsException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                )
                .onErrorResume(InvalidDataException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                );
    }

    /**
     * Registro con rol
     */
    public Mono<ServerResponse> registrarUsuario(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDTO.class)
                .flatMap(this::validateDTO)
                .flatMap(dto -> {
                    var user = userApiMapper.fromDTO(dto);
                    var roleId = String.valueOf(dto.getIdRol());
                    var plainPassword = dto.getPassword(); // puede ser null o ""
                    return userUseCase.execute(user, roleId, plainPassword);
                })
                .flatMap(user ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userApiMapper.toDTO(user))
                )
                .onErrorResume(InvalidDataException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                )
                .onErrorResume(EmailAlreadyExistsException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                )
                .onErrorResume(RoleNotFoundException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                );
    }
    /**
     * Autenticación de usuario (login)
     */
    public Mono<ServerResponse> autenticarUsuario(ServerRequest request) {
        return request.bodyToMono(LoginRequestDTO.class)
                .flatMap(dto -> {
                    if (dto.email() == null || dto.email().isBlank()) {
                        return Mono.error(new InvalidDataException("email requerido"));
                    }
                    if (dto.password() == null || dto.password().isBlank()) {
                        return Mono.error(new InvalidDataException("password requerido"));
                    }
                    var cmd = co.com.pragma.model.auth.LoginCommand.builder()
                            .email(dto.email().trim())
                            .password(dto.password())
                            .build();
                    return authenticateUserUseCase.execute(cmd);
                })
                .flatMap(authToken ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(authToken)
                )
                .onErrorResume(AuthenticateUserUseCase.InvalidCredentialsException.class, e ->
                        ServerResponse.status(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                )
                .onErrorResume(AuthenticateUserUseCase.UserDisabledException.class, e ->
                        ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                )
                .onErrorResume(InvalidDataException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                );
    }

    private Mono<UserRequestDTO> validateDTO(UserRequestDTO dto) {
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(" | "));
            return Mono.error(new InvalidDataException(errorMessage));
        }
        return Mono.just(dto);
    }
}
