package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.user.User;
import co.com.pragma.model.auth.gateways.AuthGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthRepositoryAdapter implements AuthGateway {

    private final DatabaseClient db;

    @Override
    public Mono<User> findByEmail(String email) {
        return db.sql("""
                SELECT u.id, u.email, u.enabled, c.password_hash
                FROM usuarios u
                JOIN credenciales c ON c.usuario_id = u.id
                WHERE LOWER(u.email) = LOWER(:email)
                LIMIT 1
                """)
                .bind("email", email)
                .map((row, meta) -> User.builder()
                        .id(row.get("id", Long.class))
                        .email(row.get("email", String.class))
                        .passwordHash(row.get("password_hash", String.class))
                        .enabled(row.get("enabled", Boolean.class))
                        .build())
                .one();
    }
}