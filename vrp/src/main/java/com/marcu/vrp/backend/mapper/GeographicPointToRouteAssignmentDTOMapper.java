package com.marcu.vrp.backend.mapper;

import com.marcu.vrp.backend.dto.GeographicPointDTO;
import com.marcu.vrp.backend.dto.GeographicPointToRouteAssignmentDTO;
import com.marcu.vrp.backend.model.GeographicPoint;
import com.marcu.vrp.backend.model.GeographicPointToRouteAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GeographicPointDTOMapper.class})
public interface GeographicPointToRouteAssignmentDTOMapper {
    @Mapping(target = "route", ignore = true)
    GeographicPointToRouteAssignmentDTO toDto(GeographicPointToRouteAssignment geographicPointToRouteAssignment);
    @Mapping(target = "route", ignore = true)
    GeographicPointToRouteAssignment toEntity(GeographicPointToRouteAssignmentDTO geographicPointToRouteAssignmentDTO);
}
