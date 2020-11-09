package com.marcu.vrp.backend.mapper;

import com.marcu.vrp.backend.dto.GeographicPointToRouteAssignmentDTO;
import com.marcu.vrp.backend.dto.VrpDeliveryPointDTO;
import com.marcu.vrp.backend.model.GeographicPointToRouteAssignment;
import com.marcu.vrp.backend.model.VrpDeliveryPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GeographicPointDTOMapper.class})
public interface VrpDeliveryPointDTOMapper {
    @Mapping(target = "vrpInstance", ignore = true)
    VrpDeliveryPointDTO toDto(VrpDeliveryPoint vrpDeliveryPoint);
    @Mapping(target = "vrpInstance", ignore = true)
    VrpDeliveryPoint toEntity(VrpDeliveryPointDTO vrpDeliveryPointDTO);
}
