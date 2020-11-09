package com.marcu.vrp.backend.service;

import com.marcu.vrp.backend.adapter.VrpToProviderAdapter;
import com.marcu.vrp.backend.dto.GeographicPointDTO;
import com.marcu.vrp.backend.dto.RWTDVRPSolution;
import com.marcu.vrp.backend.dto.RWTDVRPSolutionRoute;
import com.marcu.vrp.backend.dto.StartVrpSolverRequestDTO;
import com.marcu.vrp.backend.dto.UserDTO;
import com.marcu.vrp.backend.dto.VrpInstanceDTO;
import com.marcu.vrp.backend.mapper.DriverDTOMapper;
import com.marcu.vrp.backend.mapper.GeographicPointDTOMapper;
import com.marcu.vrp.backend.mapper.VrpInstanceDTOMapper;
import com.marcu.vrp.backend.model.GeographicPoint;
import com.marcu.vrp.backend.model.GeographicPointToRouteAssignment;
import com.marcu.vrp.backend.model.Route;
import com.marcu.vrp.backend.model.UserRoleName;
import com.marcu.vrp.backend.model.VrpDeliveryPoint;
import com.marcu.vrp.backend.model.VrpInstance;
import com.marcu.vrp.backend.repository.GeographicPointRepository;
import com.marcu.vrp.backend.repository.VrpInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class VrpServiceImpl implements VrpService {
    private static final Logger log = LoggerFactory.getLogger(VrpServiceImpl.class);

    private VrpInstanceRepository vrpInstanceRepository;
    private DriverDTOMapper driverDTOMapper;
    private GeographicPointDTOMapper geographicPointDTOMapper;
    private GeographicPointRepository geographicPointRepository;
    private UserService userService;
    private VrpInstanceDTOMapper vrpInstanceDTOMapper;
    private VrpToProviderAdapter<String, RWTDVRPSolution> vrpToProviderAdapter;
    private DriverService driverService;

    public VrpServiceImpl(final VrpInstanceRepository vrpInstanceRepository,
                          final DriverDTOMapper driverDTOMapper,
                          final GeographicPointDTOMapper geographicPointDTOMapper,
                          final GeographicPointRepository geographicPointRepository,
                          final UserService userService,
                          final VrpInstanceDTOMapper vrpInstanceDTOMapper,
                          final VrpToProviderAdapter<String, RWTDVRPSolution> vrpToProviderAdapter,
                          final DriverService driverService) {
        this.vrpInstanceRepository = vrpInstanceRepository;
        this.driverDTOMapper = driverDTOMapper;
        this.geographicPointDTOMapper = geographicPointDTOMapper;
        this.geographicPointRepository = geographicPointRepository;
        this.userService = userService;
        this.vrpInstanceDTOMapper = vrpInstanceDTOMapper;
        this.vrpToProviderAdapter = vrpToProviderAdapter;
        this.driverService = driverService;
    }

    @Override
    @Transactional
    public Long startVrp(StartVrpSolverRequestDTO startVrpSolverRequestDTO) {
        log.debug("User {} started a vrp solver with the following VRP details {}.", startVrpSolverRequestDTO.toString());

        //save the points
        log.debug("Saving {} Geographic Points", startVrpSolverRequestDTO.getDeliveryPoints().size() + 1);

        VrpInstance.VrpInstanceBuilder vrpInstanceBuilder = VrpInstance.builder();


        GeographicPointDTO depot = startVrpSolverRequestDTO.getDepot();

        Optional<GeographicPoint> possiblyExistingDepot = geographicPointRepository.findByAddressAndLatitudeAndLongitude(depot.getAddress(), depot.getLatitude(), depot.getLongitude());
        if (possiblyExistingDepot.isPresent()) {
            log.debug("Geographic Point {} already existing in the database!", depot.toString());
            vrpInstanceBuilder.depotId(possiblyExistingDepot.get().getId());
            vrpInstanceBuilder.depot(possiblyExistingDepot.get());
        } else {
            GeographicPoint savedDepotEntity = geographicPointRepository.save(geographicPointDTOMapper.toEntity(depot));
            vrpInstanceBuilder.depotId(savedDepotEntity.getId());
            vrpInstanceBuilder.depot(savedDepotEntity);
        }

        vrpInstanceBuilder.userId(startVrpSolverRequestDTO.getUserId());
        vrpInstanceBuilder.preferredDepartureTime(startVrpSolverRequestDTO.getPreferredDepartureTime());

        VrpInstance vrpInstance = vrpInstanceBuilder.build();

        Set<VrpDeliveryPoint> deliveryPoints = new HashSet<>();
        startVrpSolverRequestDTO.getDeliveryPoints().forEach(dp -> {
            Optional<GeographicPoint> possiblyExistingGeoPoint = geographicPointRepository.findByAddressAndLatitudeAndLongitude(dp.getAddress(), dp.getLatitude(), dp.getLongitude());
            if (possiblyExistingGeoPoint.isPresent()) {
                log.debug("Geographic Point {} already existing in the database!", possiblyExistingGeoPoint.toString());
                VrpDeliveryPoint vrpDeliveryPoint = VrpDeliveryPoint.builder()
                        .geographicPoint(possiblyExistingGeoPoint.get())
                        .vrpInstance(vrpInstance)
                        .build();
                deliveryPoints.add(vrpDeliveryPoint);
            } else {
                GeographicPoint savedGeoPoint = geographicPointRepository.save(geographicPointDTOMapper.toEntity(dp));
                VrpDeliveryPoint vrpDeliveryPoint = VrpDeliveryPoint.builder()
                        .geographicPoint(savedGeoPoint)
                        .vrpInstance(vrpInstance)
                        .build();
                deliveryPoints.add(vrpDeliveryPoint);
            }
        });
        vrpInstance.setDeliveryPoints(deliveryPoints);

        List<Route> routes = new ArrayList<>();
        startVrpSolverRequestDTO.getDrivers().forEach(driverDTO -> {
            Route route = Route.builder()
                    .driver(driverDTOMapper.toEntity(driverDTO))
                    .vrpInstance(vrpInstance)
                    .build();
            routes.add(route);
        });
        vrpInstance.setRoutes(routes);

        VrpInstance savedVrpInstance = vrpInstanceRepository.save(vrpInstance);

        vrpToProviderAdapter.doVrpRequest(vrpInstance);

        log.debug("Started a VRP solver for vrp instance with id {}", savedVrpInstance.getId());

        return savedVrpInstance.getId();
    }

    @Override
    public List<VrpInstanceDTO> findAll(Principal principal) {
        log.debug("Find all VRP instances requested");
        UserDTO userDTO = userService.loadUserByUsername(principal.getName());
        boolean viewAll = userDTO.getRole().getName().equals(UserRoleName.ADMIN) || userDTO.getRole().getName().equals(UserRoleName.DEVELOPER);
        if (viewAll) {
            log.debug("Will retrieve all vrp instances for admin or developer with id {}", userDTO.getId());
            return vrpInstanceDTOMapper.toDtos(vrpInstanceRepository.findAll());
        } else {
            log.debug("Will retrieve all vrp of the user with id {}", userDTO.getId());
            return vrpInstanceDTOMapper.toDtos(vrpInstanceRepository.findAllByUserId(userDTO.getId()));
        }
    }

    @Override
    @Transactional
    public VrpInstanceDTO findById(Long id) {
        log.debug("Loading Vrp Instance with id {}.", id);
        return vrpInstanceDTOMapper.toDto(vrpInstanceRepository.findById(id).orElseThrow(() -> new IllegalStateException("Vrp Instance with id" + id.toString() + "not found")));
    }

    @Override
    public VrpInstanceDTO stopVrp(Long id) {
        log.debug("Solver stopping was requested for vrp Instance with id {}.", id);

        VrpInstance vrpInstance = vrpInstanceRepository.findById(id).orElseThrow(() -> new IllegalStateException("Vrp Instance with id" + id.toString() + "not found"));
        RWTDVRPSolution rwtdvrpSolution = vrpToProviderAdapter.stopVrpRequest(id);

        vrpInstance.setTotalCost(rwtdvrpSolution.getTotalCost());
        vrpInstance.setSuggestedDepartureTime(rwtdvrpSolution.getDepartureTime());
        for (int i = 0; i < rwtdvrpSolution.getRoutes().size(); i++) {
            RWTDVRPSolutionRoute rwtdvrpSolutionRoute = rwtdvrpSolution.getRoutes().get(i);
            Route vrpRoute = vrpInstance.getRoutes().get(i);
            vrpRoute.setCost(rwtdvrpSolutionRoute.getCost());

            Set<GeographicPointToRouteAssignment> geographicPointToRouteAssignments = new HashSet<>();
            List<GeographicPointDTO> rwtdvrpSolutionRoutePoints = rwtdvrpSolutionRoute.getRoute();
            for (int j = 1; j < rwtdvrpSolutionRoutePoints.size() - 1; j++) {
                GeographicPointToRouteAssignment geographicPointToRouteAssignment = new GeographicPointToRouteAssignment();
                geographicPointToRouteAssignment.setIndexInRoute((long) j - 1);
                geographicPointToRouteAssignment.setRoute(vrpRoute);

                GeographicPointDTO geographicPointDTO = rwtdvrpSolutionRoutePoints.get(j);
                GeographicPoint geographicPoint = geographicPointRepository.findByAddressAndLatitudeAndLongitude(geographicPointDTO.getAddress(), geographicPointDTO.getLatitude(), geographicPointDTO.getLongitude()).orElseThrow(() -> new IllegalStateException("Geographic point with address " + geographicPointDTO.getAddress() + " and latitude " + geographicPointDTO.getLatitude() + " and longitude " + geographicPointDTO.getLongitude() + " should exist, but it does not!"));
                geographicPointToRouteAssignment.setGeographicPoint(geographicPoint);

                geographicPointToRouteAssignments.add(geographicPointToRouteAssignment);
            }

            vrpRoute.setGeographicPointToRouteAssignments(geographicPointToRouteAssignments);
        }

        return vrpInstanceDTOMapper.toDto(vrpInstanceRepository.save(vrpInstance));
    }

    @Override
    public void deleteById(Long id) {
        this.vrpInstanceRepository.deleteById(id);
    }
}
