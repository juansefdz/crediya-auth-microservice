package co.com.pragma.r2dbc.config;

import co.com.pragma.model.auth.gateways.JwtProviderGateway;
import co.com.pragma.r2dbc.adapter.JwtProviderGatewayAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityJwtConfig {

    @Bean
    public JwtProviderGateway jwtProviderGateway(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer:crediya}") String issuer,
            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        return new JwtProviderGatewayAdapter(secret, issuer, expirationSeconds);
    }
}
