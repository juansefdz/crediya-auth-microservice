package co.com.pragma.api;

import co.com.pragma.api.config.RouterConfig;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.api.mapper.UserApiMapper;
import co.com.pragma.api.service.UserTransactionalService;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {RouterConfig.class, Handler.class})
@WebFluxTest
class HandlerTest {

    @Autowired
    private WebTestClient webTestClient;


    @MockBean
    private UserTransactionalService userTransactionalService;

    @MockBean
    private UserApiMapper userApiMapper;

    @MockBean
    private jakarta.validation.Validator validator;

    @Test
    void debeRegistrarUsuarioYDevolver201() {
        // Given
        String roleId = "1";
        UserRequestDTO requestDTO = UserRequestDTO.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .documentoIdentidad("12345678")
                .salarioBase(new BigDecimal("2000000"))
                .idRol(123L)
                .build();

        Role rolGuardado = Role.builder().id(123L).nombre("ADMIN").build();
        User userGuardado = User.builder()
                .id(1L)
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .rol(rolGuardado)
                .build();

        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .nombreRol("ADMIN")
                .build();

        // When
        when(validator.validate(any(UserRequestDTO.class))).thenReturn(java.util.Collections.emptySet());


        when(userTransactionalService.registrarNuevoUsuario(any(UserRequestDTO.class), any(String.class)))
                .thenReturn(Mono.just(userGuardado));


        // Then
        webTestClient.post()
                .uri("/api/v1/usuarios/{roleId}", roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDTO.class)
                .isEqualTo(responseDTO);
    }
}