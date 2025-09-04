package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.auth.gateways.JwtProviderGateway;
import co.com.pragma.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

public class JwtProviderGatewayAdapter implements JwtProviderGateway {

    private static final String ROLE_CLAIM = "role";

    private final SecretKey key;
    private final String issuer;
    private final long expirationSeconds;

    public JwtProviderGatewayAdapter(String secret, String issuer, long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public Mono<String> generateAccessToken(User user) {
        return Mono.fromSupplier(() -> {
            String subject = Optional.ofNullable(user.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User email (subject) is required"));

            String role = Optional.ofNullable(user.getRol())
                    .map(r -> r.getNombre())
                    .orElse("USER");

            Date now = new Date();
            Date exp = new Date(now.getTime() + (expirationSeconds * 1000));

            return Jwts.builder()
                    .subject(subject)
                    .issuer(issuer)
                    .issuedAt(now)
                    .expiration(exp)
                    .claim(ROLE_CLAIM, role)
                    .signWith(key, Jwts.SIG.HS256)
                    .compact();
        });
    }

    @Override
    public Mono<Boolean> validate(String token) {
        return Mono.fromSupplier(() -> {
            try {
                Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }

    @Override
    public Mono<String> getSubject(String token) {
        return Mono.fromSupplier(() -> parseClaims(token).getSubject());
    }

    @Override
    public Mono<String> getRole(String token) {
        return Mono.fromSupplier(() -> {
            Object role = parseClaims(token).get(ROLE_CLAIM);
            return role != null ? role.toString() : null;
        });
    }

    @Override
    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
