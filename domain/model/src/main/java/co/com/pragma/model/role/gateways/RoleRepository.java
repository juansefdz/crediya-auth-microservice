package co.com.pragma.model.role.gateways;


import co.com.pragma.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> findById(String id);
    Mono<Role> findByNombre(String nombre);
    Flux<Role> getRolesByUserId(Long userId);
}