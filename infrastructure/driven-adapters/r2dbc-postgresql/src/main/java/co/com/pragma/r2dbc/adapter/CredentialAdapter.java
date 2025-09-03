package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.auth.Credential;
import co.com.pragma.model.auth.gateways.CredentialGateway;
import co.com.pragma.r2dbc.mapper.CredentialPersistenceMapper;
import co.com.pragma.r2dbc.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class CredentialAdapter implements CredentialGateway {

    private final CredentialRepository repository;
    private final CredentialPersistenceMapper mapper;

    @Override
    public Mono<Credential> save(Credential credential) {
        return repository.save(mapper.toEntity(credential))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}