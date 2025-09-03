package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserResponseDTO;
import org.mapstruct.Mapper;

import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapping;

import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserApiMapper {

    @Mapping(target = "rol", ignore = true)
    User fromDTO(UserRequestDTO dto);


    @Mapping(target = "nombreRol", source = "rol.nombre")
    UserResponseDTO toDTO(User user);
}