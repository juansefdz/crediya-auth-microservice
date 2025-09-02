package co.com.pragma.api.service;

import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.mapper.UserApiMapper;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserTransactionalService {

    private final UserUseCase userUseCase;
    private final TransactionalOperator transactionalOperator;
    private final UserApiMapper userApiMapper;

    public Mono<User> registrarNuevoUsuario(UserRequestDTO userRequest, String roleId) {

        User userDraft = userApiMapper.fromDTO(userRequest);
        Mono<User> useCaseExecution = userUseCase.execute(userDraft, roleId);

        return transactionalOperator.transactional(useCaseExecution);
    }
}