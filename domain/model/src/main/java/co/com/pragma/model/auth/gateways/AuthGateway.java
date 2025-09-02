package co.com.pragma.model.auth.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface AuthGateway {
    Mono<User> findByEmail(String email);
}
