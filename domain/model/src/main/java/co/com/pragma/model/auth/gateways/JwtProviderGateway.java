package co.com.pragma.model.auth.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface JwtProviderGateway {
    Mono<String> generateAccessToken(User user);
    Mono<Boolean> validate(String token);
    Mono<String> getSubject(String token);  // email
    Mono<String> getRole(String token);
    long getExpirationSeconds();
}
