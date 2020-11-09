package com.marcu.vrp.backend.adapter;

import com.marcu.vrp.backend.dto.RWTDVRPRequestObject;
import com.marcu.vrp.backend.dto.RWTDVRPSolution;
import com.marcu.vrp.backend.mapper.GeographicPointDTOMapper;
import com.marcu.vrp.backend.model.VrpDeliveryPoint;
import com.marcu.vrp.backend.model.VrpInstance;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

@Component
public class RWTDVRPAdapter implements VrpToProviderAdapter<String, RWTDVRPSolution> {
    private GeographicPointDTOMapper geographicPointDTOMapper;

    public RWTDVRPAdapter(final GeographicPointDTOMapper geographicPointDTOMapper) {
        this.geographicPointDTOMapper = geographicPointDTOMapper;
    }

    @Override
    public String doVrpRequest(VrpInstance vrpInstance) {
        RWTDVRPRequestObject rwtdvrpRequestObject = transformVrpInstanceToRWTDVRPRequestObject(vrpInstance);

        RestTemplate restTemplate = new RestTemplate();
        String startVrpUrl = "http://localhost:8081/api/rwtdvrp/start";
        HttpEntity<RWTDVRPRequestObject> request = new HttpEntity<>(rwtdvrpRequestObject);

        String id = restTemplate.postForObject(startVrpUrl, request, String.class);
        if (id == null || id.isEmpty())  {
            throw new InvalidDataAccessApiUsageException("Error when communicating with the RWTDVRP Solver API");
        }
        return id;
    }

    @Override
    public RWTDVRPSolution stopVrpRequest(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String stopVrpUrl = "http://localhost:8081/api/rwtdvrp/stop/" + id.toString();
        RWTDVRPSolution rwtdvrpSolution = restTemplate.getForObject(stopVrpUrl, RWTDVRPSolution.class);
        if (rwtdvrpSolution == null || rwtdvrpSolution.getDepartureTime() == null || rwtdvrpSolution.getRoutes() == null || rwtdvrpSolution.getTotalCost() == null) {
            throw new InvalidDataAccessApiUsageException("Error when communicating with the RWTDVRP Solver API");
        }
        return rwtdvrpSolution;
    }

    private RWTDVRPRequestObject transformVrpInstanceToRWTDVRPRequestObject(VrpInstance vrpInstance) {
        return RWTDVRPRequestObject.builder()
                .id(vrpInstance.getId().toString())
                .depot(geographicPointDTOMapper.toDto(vrpInstance.getDepot()))
                .numberOfDrivers(vrpInstance.getRoutes().size())
                .preferredDepartureTime(vrpInstance.getPreferredDepartureTime())
                .deliveryPoints(vrpInstance.getDeliveryPoints().stream().map(VrpDeliveryPoint::getGeographicPoint).map(gp -> geographicPointDTOMapper.toDto(gp)).collect(Collectors.toList()))
                .build();
    }
}
