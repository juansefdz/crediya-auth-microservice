package co.com.pragma.usecase;

import co.com.pragma.model.customExceptions.BusinessException;
import co.com.pragma.model.customExceptions.EmailAlreadyExistsException;
import co.com.pragma.model.customExceptions.InvalidSalaryException;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    @Test
    void testCreacionUsuarioExitoso() {
        // Given
        String roleId = "2";
        User userDraft = User.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .salarioBase(new BigDecimal("2000000"))
                .build();

        Role roleMock = Role.builder().id(2L).nombre("ADMIN").build();
        User userSavedMock = userDraft.toBuilder()
                .id(987L)
                .rol(roleMock)
                .build();
        when(roleRepository.findById(roleId)).thenReturn(Mono.just(roleMock));
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentoIdentidad(anyString())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userSavedMock));

        // Then
        Mono<User> resultMono = userUseCase.execute(userDraft, roleId);

        StepVerifier.create(resultMono)
                .expectNextMatches(savedUser -> {
                    return savedUser.getId().equals(987L) &&
                            savedUser.getRol().getNombre().equals("ADMIN");
                })
                .verifyComplete();
    }


    @Test
    void testFalloRolNoExiste() {
        // Given
        String roleIdQueNoExiste = "3";
        User userDraft = User.builder()
                .email("test@example.com")
                .salarioBase(new BigDecimal("1000"))
                .build();

        // When
        when(roleRepository.findById(roleIdQueNoExiste)).thenReturn(Mono.empty());
        // Then
        Mono<User> resultMono = userUseCase.execute(userDraft, roleIdQueNoExiste);

        StepVerifier.create(resultMono)
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void testFalloEmailExistente() {
        // Given
        String roleId = "1";
        User userDraft = User.builder()
                .nombre("Ana")
                .apellidos("Gomez")
                .email("ana.gomez@example.com")
                .documentoIdentidad("87654321")
                .salarioBase(new BigDecimal("3000000"))
                .build();

        Role roleMock = Role.builder().id(1L).nombre("USER").build();

        // When
        when(roleRepository.findById(roleId)).thenReturn(Mono.just(roleMock));
        when(userRepository.existsByEmail(userDraft.getEmail())).thenReturn(Mono.just(true));
        when(userRepository.existsByDocumentoIdentidad(userDraft.getDocumentoIdentidad())).thenReturn(Mono.just(false));
        // Then
        Mono<User> resultMono = userUseCase.execute(userDraft, roleId);

        StepVerifier.create(resultMono)
                .expectError(EmailAlreadyExistsException.class)
                .verify();
    }

    @Test
    void testFalloSalarioInvalido() {
        // Given
        String roleId = "1";
        User userDraft = User.builder()
                .email("nuevo.usuario@example.com")
                .salarioBase(new BigDecimal("-5000")) // Salario inválido
                .build();
        Role roleMock = Role.builder().id(1L).nombre("USER").build();
        // When
        when(roleRepository.findById(roleId)).thenReturn(Mono.just(roleMock));
        // Then
        Mono<User> resultMono = userUseCase.execute(userDraft, roleId);

        StepVerifier.create(resultMono)
                .expectError(InvalidSalaryException.class)
                .verify();
    }
}