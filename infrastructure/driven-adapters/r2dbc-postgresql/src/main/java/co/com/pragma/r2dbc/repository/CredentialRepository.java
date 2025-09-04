package co.com.pragma.r2dbc.repository;


import co.com.pragma.model.auth.Credential;
import co.com.pragma.r2dbc.data.CredentialData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CredentialRepository extends R2dbcRepository<CredentialData, Long> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Credential> findByEmail(String email);
}