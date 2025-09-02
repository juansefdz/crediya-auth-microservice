package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.data.UserData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserDataRepository extends ReactiveCrudRepository<UserData, Long> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDocumentoIdentidad(String documentoIdentidad);

    Mono<Boolean> existsByEmailIgnoreCase(String email);
}