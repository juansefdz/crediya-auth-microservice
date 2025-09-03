package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.r2dbc.mapper.RolePersistenceMapper;
import co.com.pragma.r2dbc.repository.RoleDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleDataRepository repository;
    private final RolePersistenceMapper mapper;

    @Override
    public Mono<Role> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RoleNotFoundException("No existe rol con id=" + id)))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Role> findByNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre)
                .switchIfEmpty(Mono.error(new RoleNotFoundException("No existe rol con nombre=" + nombre)))
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Role> getRolesByUserId(Long userId) {

        return repository.findAllByUserId(userId)
                .map(mapper::toDomain)
                .switchIfEmpty(Flux.error(new RoleNotFoundException("Usuario " + userId + " no tiene rol")));
    }

    // Si igual quieres exponer un Mono:
    public Mono<Role> getByUserId(Long userId) {
        return repository.findAllByUserId(userId)
                .next()
                .switchIfEmpty(Mono.error(new RoleNotFoundException("Usuario " + userId + " no tiene rol")))
                .map(mapper::toDomain);
    }

    static class RoleNotFoundException extends RuntimeException {
        RoleNotFoundException(String msg) { super(msg); }
    }
}
