package co.com.pragma.service;

import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.service.UserTransactionalService;
import co.com.pragma.model.customExceptions.BusinessException;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.Test;

import co.com.pragma.api.mapper.UserApiMapper;
import co.com.pragma.usecase.user.UserUseCase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserTransactionalServiceTest {
    @Mock
    private UserUseCase userUseCase;
    @Mock
    private TransactionalOperator transactionalOperator;
    @Mock
    private UserApiMapper userApiMapper;
    @InjectMocks
    private UserTransactionalService userTransactionalService;

    @Test
    void testPropagacionErrorUseCase() {
        // Given
        UserRequestDTO userRequest = new UserRequestDTO(/* ... */);
        String roleId = "123";
        User userDraft = User.builder().build();
        BusinessException businessException = new BusinessException("Error de negocio simulado");

        // When
        when(userApiMapper.fromDTO(userRequest)).thenReturn(userDraft);
        when(userUseCase.execute(userDraft, roleId)).thenReturn(Mono.error(businessException));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Then
        Mono<User> resultMono = userTransactionalService.registrarNuevoUsuario(userRequest, roleId);

        StepVerifier.create(resultMono)
                .expectError(BusinessException.class)
                .verify();
    }
}
