package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.auth.gateways.PermissionGateway;
import co.com.pragma.r2dbc.repository.PermissionDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class PermissionGatewayAdapter implements PermissionGateway {

    private final PermissionDataRepository repository;

    @Override
    public Flux<String> getPermissionsByRoleId(Long roleId) {
        return repository.findPermissionNamesByRoleId(roleId);
    }
}