package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByDocumentoIdentidad(String documento);
    Mono<User> save(User user);
    Mono<User> findByEmail(String email);

    Mono<Long> count();
}
