package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.data.UserData;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserPersistenceMapper {

    // DOMAIN -> DATA
            @Mapping(target = "idRol",              source = "rol.id")

    UserData toData(User user);

    // DATA -> DOMAIN (sin rol)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "id",                 source = "userData.id"),
            @Mapping(target = "nombre",             source = "userData.nombre"),
            @Mapping(target = "apellidos",          source = "userData.apellidos"),
            @Mapping(target = "documentoIdentidad", source = "userData.documentoIdentidad"),
            @Mapping(target = "fechaNacimiento",    source = "userData.fechaNacimiento"),
            @Mapping(target = "direccion",          source = "userData.direccion"),
            @Mapping(target = "email",              source = "userData.email"),
            @Mapping(target = "telefono",           source = "userData.telefono"),
            @Mapping(target = "salarioBase",        source = "userData.salarioBase"),
            @Mapping(target = "enabled",            source = "userData.enabled"), // 👈
            @Mapping(target = "rol",                ignore = true)
    })
    User toDomain(UserData userData);

    // DATA -> DOMAIN (con rol)
    @BeanMapping(ignoreByDefault = true)
    @Mappings({
            @Mapping(target = "id",                 source = "userData.id"),
            @Mapping(target = "nombre",             source = "userData.nombre"),
            @Mapping(target = "apellidos",          source = "userData.apellidos"),
            @Mapping(target = "documentoIdentidad", source = "userData.documentoIdentidad"),
            @Mapping(target = "fechaNacimiento",    source = "userData.fechaNacimiento"),
            @Mapping(target = "direccion",          source = "userData.direccion"),
            @Mapping(target = "email",              source = "userData.email"),
            @Mapping(target = "telefono",           source = "userData.telefono"),
            @Mapping(target = "salarioBase",        source = "userData.salarioBase"),
            @Mapping(target = "enabled",            source = "userData.enabled"), // 👈
            @Mapping(target = "rol",                source = "role")
    })
    User toDomain(UserData userData, Role role);
}
