package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.data.RoleData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleDataRepository extends ReactiveCrudRepository<RoleData, Long> {
    Mono<RoleData> findByNombreIgnoreCase(String nombre);

    @Query("""
           SELECT r.id, r.nombre, r.descripcion
           FROM roles r
           JOIN usuarios u ON u.rol_id = r.id
           WHERE u.id = :userId
           """)
    Flux<RoleData> findAllByUserId(Long userId);
}
