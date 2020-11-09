package com.marcu.vrp.backend.mapper;

import com.marcu.vrp.backend.dto.DriverDTO;
import com.marcu.vrp.backend.dto.UserRoleDTO;
import com.marcu.vrp.backend.model.Driver;
import com.marcu.vrp.backend.model.UserRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverDTOMapper {
    DriverDTO toDto(Driver driver);
    Driver toEntity(DriverDTO driverDTO);
    List<DriverDTO> toDtos(List<Driver> drivers);
}
