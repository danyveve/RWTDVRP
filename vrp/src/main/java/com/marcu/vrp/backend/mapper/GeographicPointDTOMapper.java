package com.marcu.vrp.backend.mapper;

import com.marcu.vrp.backend.dto.GeographicPointDTO;
import com.marcu.vrp.backend.dto.RouteDTO;
import com.marcu.vrp.backend.model.GeographicPoint;
import com.marcu.vrp.backend.model.Route;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GeographicPointDTOMapper {
    GeographicPointDTO toDto(GeographicPoint geographicPoint);
    GeographicPoint toEntity(GeographicPointDTO geographicPointDTO);
}
