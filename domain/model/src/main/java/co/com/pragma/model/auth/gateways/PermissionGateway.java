package co.com.pragma.model.auth.gateways;

import reactor.core.publisher.Flux;

public interface PermissionGateway {
    Flux<String> getPermissionsByRoleId(Long roleId);
}