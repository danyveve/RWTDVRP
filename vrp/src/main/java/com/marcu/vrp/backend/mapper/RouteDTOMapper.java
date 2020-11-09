package com.marcu.vrp.backend.mapper;

import com.marcu.vrp.backend.dto.RouteDTO;
import com.marcu.vrp.backend.model.GeographicPointToRouteAssignment;
import com.marcu.vrp.backend.model.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DriverDTOMapper.class, GeographicPointToRouteAssignmentDTOMapper.class})
public interface RouteDTOMapper {
    @Mapping(target = "vrpInstance", ignore = true)
    RouteDTO toDto(Route route);
    @Mapping(target = "vrpInstance", ignore = true)
    Route toEntity(RouteDTO routeDTO);
}
