package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.data.PermissionData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PermissionDataRepository extends ReactiveCrudRepository<PermissionData, Long> {

    @Query("SELECT p.name FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = :roleId")
    Flux<String> findPermissionNamesByRoleId(Long roleId);
}