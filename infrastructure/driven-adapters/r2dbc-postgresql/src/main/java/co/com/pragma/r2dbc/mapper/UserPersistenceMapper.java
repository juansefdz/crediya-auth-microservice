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


    @Mappings({
            @Mapping(target = "id",                 source = "id"),
            @Mapping(target = "nombre",             source = "nombre"),
            @Mapping(target = "apellidos",          source = "apellidos"),
            @Mapping(target = "documentoIdentidad", source = "documentoIdentidad"),
            @Mapping(target = "fechaNacimiento",    source = "fechaNacimiento"),
            @Mapping(target = "direccion",          source = "direccion"),
            @Mapping(target = "email",              source = "email"),
            @Mapping(target = "telefono",           source = "telefono"),
            @Mapping(target = "salarioBase",        source = "salarioBase"),
            @Mapping(target = "idRol",              source = "rol.id") // FK
    })
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
            @Mapping(target = "rol",                ignore = true) // se setea aparte
    })
    User toDomain(UserData userData);


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
            @Mapping(target = "rol",                source = "role")
    })
    User toDomain(UserData userData, Role role);
}
