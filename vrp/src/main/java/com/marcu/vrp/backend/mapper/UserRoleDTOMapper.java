package com.marcu.vrp.backend.mapper;

import com.marcu.vrp.backend.dto.UserRoleDTO;
import com.marcu.vrp.backend.model.UserRole;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRoleDTOMapper {
    UserRoleDTO toDto(UserRole userRole);
    UserRole toEntity(UserRoleDTO userRoleDTO);
}
