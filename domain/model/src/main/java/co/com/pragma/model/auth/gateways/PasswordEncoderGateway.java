package co.com.pragma.model.auth.gateways;

import reactor.core.publisher.Mono;

public interface PasswordEncoderGateway {

    Mono<String> encode(CharSequence rawPassword);


    Mono<Boolean> matches(CharSequence rawPassword, String encodedPassword);
}