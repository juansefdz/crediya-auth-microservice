package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.auth.Credential;
import co.com.pragma.model.auth.gateways.CredentialGateway;
import co.com.pragma.r2dbc.mapper.CredentialPersistenceMapper;
import co.com.pragma.r2dbc.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class CredentialAdapter implements CredentialGateway {

    private final CredentialRepository repository;
    private final CredentialPersistenceMapper mapper;
    private final DatabaseClient client;

    @Override
    public Mono<Credential> save(Credential credential) {
        return repository.save(mapper.toEntity(credential))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<Void> upsert(String email, String passwordHash, Long usuarioId) {
        String sql = """
        INSERT INTO credentials (usuario_id, email, password_hash, enabled)
        VALUES (:uid, :email, :hash, TRUE)
        ON CONFLICT (email)
        DO UPDATE SET password_hash = EXCLUDED.password_hash,
                      enabled = TRUE,
                      usuario_id = EXCLUDED.usuario_id
        """;
        return client.sql(sql)
                .bind("uid", usuarioId)
                .bind("email", email)
                .bind("hash", passwordHash)
                .then();
    }
}