package co.com.pragma.api.mapper;


import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserApiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rol", ignore = true)
    User fromDTO(UserRequestDTO dto);

    @Mapping(target = "nombreRol", source = "user.rol.nombre")
    UserResponseDTO toDTO(User user);
}