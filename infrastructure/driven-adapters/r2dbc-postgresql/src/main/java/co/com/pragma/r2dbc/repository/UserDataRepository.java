package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.data.UserData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserDataRepository extends ReactiveCrudRepository<UserData, Long> {

    Mono<Boolean> existsByDocumentoIdentidad(String documentoIdentidad);
    Mono<UserData> findByEmailIgnoreCase(String email);
    Mono<Boolean> existsByDocumentoIdentidadAndIdNot(String documento, Long excludeId);
    Mono<Boolean> existsByEmailIgnoreCase(String email);

}