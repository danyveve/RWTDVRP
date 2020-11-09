package com.marcu.vrp.backend.mapper;

import com.marcu.vrp.backend.dto.RegisterUserDTO;
import com.marcu.vrp.backend.dto.UserDTO;
import com.marcu.vrp.backend.dto.UserRoleDTO;
import com.marcu.vrp.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserRoleDTO.class})
public interface UserDTOMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUserFromUserRegisterDTO(RegisterUserDTO registerUserDTO);
}
