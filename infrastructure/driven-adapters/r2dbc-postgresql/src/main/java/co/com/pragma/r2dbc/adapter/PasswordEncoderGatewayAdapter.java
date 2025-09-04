package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;



@Component
@RequiredArgsConstructor
public class PasswordEncoderGatewayAdapter implements PasswordEncoderGateway {

    private final PasswordEncoder delegate;

    @Override
    public Mono<String> encode(CharSequence rawPassword) {
        return Mono.fromSupplier(() -> delegate.encode(rawPassword));
    }

    @Override
    public Mono<Boolean> matches(CharSequence rawPassword, String encodedPassword) {
        return Mono.fromSupplier(() -> delegate.matches(rawPassword, encodedPassword));
    }
}