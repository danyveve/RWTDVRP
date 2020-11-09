package com.marcu.vrp;

import com.marcu.vrp.backend.adapter.VrpToProviderAdapter;
import com.marcu.vrp.backend.dto.DriverDTO;
import com.marcu.vrp.backend.dto.GeographicPointDTO;
import com.marcu.vrp.backend.dto.RWTDVRPSolution;
import com.marcu.vrp.backend.dto.StartVrpSolverRequestDTO;
import com.marcu.vrp.backend.dto.VrpInstanceDTO;
import com.marcu.vrp.backend.model.Driver;
import com.marcu.vrp.backend.model.GeographicPoint;
import com.marcu.vrp.backend.model.GeographicPointToRouteAssignment;
import com.marcu.vrp.backend.model.Route;
import com.marcu.vrp.backend.model.VrpDeliveryPoint;
import com.marcu.vrp.backend.model.VrpInstance;
import com.marcu.vrp.backend.repository.GeographicPointRepository;
import com.marcu.vrp.backend.repository.VrpInstanceRepository;
import com.marcu.vrp.backend.service.VrpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VrpServiceTest {
    @Autowired
    private VrpService vrpService;

    @MockBean
    private VrpInstanceRepository vrpInstanceRepository;

    @MockBean
    private GeographicPointRepository geographicPointRepository;

    @MockBean
    private VrpToProviderAdapter<String, RWTDVRPSolution> vrpToProviderAdapter;

    private GeographicPoint depot;
    private GeographicPoint gp1;
    private GeographicPoint gp2;
    private VrpInstance vrpInstance;
    StartVrpSolverRequestDTO startVrpSolverRequestDTO;

    @BeforeEach
    public void setup() {
        depot = GeographicPoint.builder().latitude(0D).longitude(0D).address("address1").build();
        gp1 = GeographicPoint.builder().latitude(11.11D).longitude(-11.11D).address("gp1").build();
        gp2 = GeographicPoint.builder().latitude(22.22D).longitude(-22.22D).address("gp2").build();
        gp1.setId(1L);
        gp2.setId(2L);
        depot.setId(3L);

        Driver driver1 = Driver.builder().firstName("firstName1").lastName("lastName1").phone("+0111").email("email1@gmail.com").car("car1").build();
        Driver driver2 = Driver.builder().firstName("firstName2").lastName("lastName2").phone("+0222").email("email2@gmail.com").car("car2").build();
        driver1.setId(1L);
        driver2.setId(2L);

        //vrpInstance
        vrpInstance = VrpInstance.builder().build();
        vrpInstance.setDepotId(depot.getId());
        Instant preferredDeparture = Instant.now();
        vrpInstance.setPreferredDepartureTime(preferredDeparture);
        Instant suggestedDeparture = Instant.now().plus(1, ChronoUnit.DAYS);
        vrpInstance.setSuggestedDepartureTime(suggestedDeparture);

        //routes
        List<Route> routes = new ArrayList<>();
        Route route1 = Route.builder().build();
        route1.setVrpInstance(vrpInstance);
        route1.setCost(50L);
        route1.setDriver(driver1);
        Set<GeographicPointToRouteAssignment> geographicPointToRouteAssignments1 = new HashSet<>();
        GeographicPointToRouteAssignment gptra1 = GeographicPointToRouteAssignment.builder().indexInRoute(0L).route(route1).geographicPoint(gp1).build();
        geographicPointToRouteAssignments1.add(gptra1);
        route1.setGeographicPointToRouteAssignments(geographicPointToRouteAssignments1);
        routes.add(route1);
        Route route2 = Route.builder().build();
        route2.setVrpInstance(vrpInstance);
        route2.setCost(40L);
        route2.setDriver(driver2);
        Set<GeographicPointToRouteAssignment> geographicPointToRouteAssignments2 = new HashSet<>();
        GeographicPointToRouteAssignment gptra2 = GeographicPointToRouteAssignment.builder().indexInRoute(0L).route(route2).geographicPoint(gp2).build();
        geographicPointToRouteAssignments2.add(gptra2);
        route2.setGeographicPointToRouteAssignments(geographicPointToRouteAssignments2);
        routes.add(route2);
        vrpInstance.setRoutes(routes);
        //routes


        //deliveryPoints
        Set<VrpDeliveryPoint> vrpDeliveryPoints = new HashSet<>();
        VrpDeliveryPoint vrpDeliveryPoint1 = VrpDeliveryPoint.builder().build();
        vrpDeliveryPoint1.setVrpInstance(vrpInstance);
        vrpDeliveryPoint1.setGeographicPoint(gp1);
        vrpDeliveryPoints.add(vrpDeliveryPoint1);
        VrpDeliveryPoint vrpDeliveryPoint2 = VrpDeliveryPoint.builder().build();
        vrpDeliveryPoint2.setVrpInstance(vrpInstance);
        vrpDeliveryPoint2.setGeographicPoint(gp2);
        vrpDeliveryPoints.add(vrpDeliveryPoint2);
        vrpInstance.setDeliveryPoints(vrpDeliveryPoints);
        //deliveryPoints

        Long totalCost = 500L;
        vrpInstance.setTotalCost(totalCost);

        vrpInstance.setId(1L);
        //vrpInstance

        //vrp solver req
        startVrpSolverRequestDTO = StartVrpSolverRequestDTO.builder().build();
        GeographicPointDTO depot = GeographicPointDTO.builder().latitude(0D).longitude(0D).address("address1").build();
        startVrpSolverRequestDTO.setDepot(depot);
        startVrpSolverRequestDTO.setPreferredDepartureTime(preferredDeparture);
        List<GeographicPointDTO> deliveryPoints = new ArrayList<>();
        GeographicPointDTO dp1 = GeographicPointDTO.builder().latitude(11.11D).longitude(-11.11D).address("dp1").build();
        GeographicPointDTO dp2 = GeographicPointDTO.builder().latitude(22.22D).longitude(-22.22D).address("dp2").build();
        GeographicPointDTO dp3 = GeographicPointDTO.builder().latitude(33.33D).longitude(-33.33D).address("dp3").build();
        deliveryPoints.add(dp1);
        deliveryPoints.add(dp2);
        deliveryPoints.add(dp3);
        startVrpSolverRequestDTO.setDeliveryPoints(deliveryPoints);
        List<DriverDTO> driverDTOS = new ArrayList<>();
        DriverDTO driverDTO1 = DriverDTO.builder().firstName("firstName1").lastName("lastName1").phone("+0111").email("email1@gmail.com").car("car1").build();
        DriverDTO driverDTO2 = DriverDTO.builder().firstName("firstName2").lastName("lastName2").phone("+0222").email("email2@gmail.com").car("car2").build();
        DriverDTO driverDTO3 = DriverDTO.builder().firstName("firstName3").lastName("lastName3").phone("+0333").email("email3@gmail.com").car("car3").build();
        driverDTOS.add(driverDTO1);
        driverDTOS.add(driverDTO2);
        driverDTOS.add(driverDTO3);
        startVrpSolverRequestDTO.setDrivers(driverDTOS);
        //vrp solver req
    }

    @Test
    @Transactional
    public void test_startVrp_service() {
        given(geographicPointRepository.save(any())).willReturn(gp1);
        given(geographicPointRepository.save(any())).willReturn(gp2);
        given(geographicPointRepository.save(any())).willReturn(depot);
        given(geographicPointRepository.findByAddressAndLatitudeAndLongitude(depot.getAddress(), depot.getLatitude(), depot.getLongitude())).willReturn(Optional.empty());
        given(geographicPointRepository.findByAddressAndLatitudeAndLongitude(gp1.getAddress(), gp1.getLatitude(), gp1.getLongitude())).willReturn(Optional.empty());
        given(geographicPointRepository.findByAddressAndLatitudeAndLongitude(gp2.getAddress(), gp2.getLatitude(), gp2.getLongitude())).willReturn(Optional.empty());
        given(vrpToProviderAdapter.doVrpRequest(any())).willReturn(vrpInstance.getId().toString());
        given(vrpInstanceRepository.save(any())).willReturn(vrpInstance);

        Long id = vrpService.startVrp(startVrpSolverRequestDTO);
        assertEquals(vrpInstance.getId(), id);
    }

    @Test
    @Transactional
    public void test_stopVrp_service() {
        given(vrpInstanceRepository.save(any())).willReturn(vrpInstance);
        given(vrpInstanceRepository.findById(vrpInstance.getId())).willReturn(Optional.of(vrpInstance));
        given(vrpToProviderAdapter.stopVrpRequest(any())).willReturn(RWTDVRPSolution.builder().routes(new ArrayList<>()).build());
        VrpInstanceDTO vrpInstanceDTO = vrpService.stopVrp(vrpInstance.getId());
        assertEquals(vrpInstance.getId(), vrpInstanceDTO.getId());
    }
}



