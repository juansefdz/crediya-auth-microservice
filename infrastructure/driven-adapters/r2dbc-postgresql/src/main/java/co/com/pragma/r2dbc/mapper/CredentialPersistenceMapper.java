package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.auth.Credential;
import co.com.pragma.r2dbc.data.CredentialData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CredentialPersistenceMapper {

    @Mapping(source = "usuarioId", target = "userId")
    @Mapping(source = "passwordHash", target = "passwordHash")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "enabled", target = "enabled")
    CredentialData toEntity(Credential credential);

    @Mapping(source = "userId", target = "usuarioId")
    @Mapping(source = "passwordHash", target = "passwordHash")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "enabled", target = "enabled")
    Credential toDomain(CredentialData entity);
}