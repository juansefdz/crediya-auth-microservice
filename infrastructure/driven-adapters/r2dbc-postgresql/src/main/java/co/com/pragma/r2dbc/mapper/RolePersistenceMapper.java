package co.com.pragma.r2dbc.mapper;


import co.com.pragma.model.role.Role;
import co.com.pragma.r2dbc.data.RoleData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RolePersistenceMapper {
    RoleData toData(Role role);
    Role toDomain(RoleData roleData);
}