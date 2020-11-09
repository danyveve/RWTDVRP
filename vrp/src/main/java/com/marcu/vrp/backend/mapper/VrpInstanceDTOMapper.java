package com.marcu.vrp.backend.mapper;

import com.marcu.vrp.backend.dto.VrpInstanceDTO;
import com.marcu.vrp.backend.model.VrpInstance;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserDTOMapper.class, RouteDTOMapper.class, GeographicPointDTOMapper.class, VrpDeliveryPointDTOMapper.class})
public interface VrpInstanceDTOMapper {
    VrpInstance toEntity(VrpInstanceDTO vrpInstanceDTO);
    VrpInstanceDTO toDto(VrpInstance vrpInstance);
    List<VrpInstanceDTO> toDtos(List<VrpInstance> vrpInstanceList);
}
