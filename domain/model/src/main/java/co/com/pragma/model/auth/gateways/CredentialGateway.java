package co.com.pragma.model.auth.gateways;


import co.com.pragma.model.auth.Credential;
import reactor.core.publisher.Mono;

public interface CredentialGateway {
    Mono<Credential> save(Credential credential);
    Mono<Boolean> existsByEmail(String email);
}
