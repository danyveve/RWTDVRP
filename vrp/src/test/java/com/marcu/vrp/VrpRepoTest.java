package com.marcu.vrp;

import com.marcu.vrp.backend.model.Driver;
import com.marcu.vrp.backend.model.GeographicPoint;
import com.marcu.vrp.backend.model.GeographicPointToRouteAssignment;
import com.marcu.vrp.backend.model.Route;
import com.marcu.vrp.backend.model.User;
import com.marcu.vrp.backend.model.UserRole;
import com.marcu.vrp.backend.model.UserRoleName;
import com.marcu.vrp.backend.model.VrpDeliveryPoint;
import com.marcu.vrp.backend.model.VrpInstance;
import com.marcu.vrp.backend.repository.DriverRepository;
import com.marcu.vrp.backend.repository.GeographicPointRepository;
import com.marcu.vrp.backend.repository.UserRepository;
import com.marcu.vrp.backend.repository.UserRoleRepository;
import com.marcu.vrp.backend.repository.VrpInstanceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VrpRepoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private VrpInstanceRepository vrpInstanceRepository;

    @Autowired
    private GeographicPointRepository geographicPointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private GeographicPoint depot;
    private GeographicPoint gp1;
    private GeographicPoint gp2;
    private Driver driver1;
    private Driver driver2;
    private User user;
    private VrpInstance vrpInstance;

    @BeforeEach
    public void setup() {
        UserRole userRole = UserRole.builder()
                .name(UserRoleName.CLIENT)
                .build();
        Long userRoleId = userRoleRepository.save(userRole).getId();

        user = User.builder()
                .username("test-user")
                .password(new BCryptPasswordEncoder().encode("test"))
                .email("test@gmail.com")
                .firstName("").lastName("").phone("")
                .roleId(userRoleId)
                .build();
        user = userRepository.save(user);

        depot = GeographicPoint.builder().latitude(0D).longitude(0D).address("address1").build();
        depot = geographicPointRepository.save(depot);
        gp1 = GeographicPoint.builder().latitude(11.11D).longitude(-11.11D).address("gp1").build();
        gp2 = GeographicPoint.builder().latitude(22.22D).longitude(-22.22D).address("gp2").build();
        gp1 = geographicPointRepository.save(gp1);
        gp2 = geographicPointRepository.save(gp2);

        driver1 = Driver.builder().firstName("firstName1").lastName("lastName1").phone("+0111").email("email1@gmail.com").car("car1").build();
        driver2 = Driver.builder().firstName("firstName2").lastName("lastName2").phone("+0222").email("email2@gmail.com").car("car2").build();
        driver1 = driverRepository.save(driver1);
        driver2 = driverRepository.save(driver2);

        //vrpInstance
        vrpInstance = VrpInstance.builder().build();
        vrpInstance.setDepotId(depot.getId());
        Instant preferredDeparture = Instant.now();
        vrpInstance.setPreferredDepartureTime(preferredDeparture);
        Instant suggestedDeparture = Instant.now().plus(1, ChronoUnit.DAYS);
        vrpInstance.setSuggestedDepartureTime(suggestedDeparture);
        vrpInstance.setUserId(user.getId());

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
        //vrpInstance
    }

    @AfterEach
    public void tearDown() {
        //enables test methods to use multiple transactions (if required), while still ensuring proper cleanup
        final TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute((TransactionCallback<Void>) status -> {
            jdbcTemplate.execute("DELETE FROM delivery_point");
            jdbcTemplate.execute("DELETE FROM geographic_point_to_route_assignment");
            jdbcTemplate.execute("DELETE FROM route");
            jdbcTemplate.execute("DELETE FROM vrp_instance");
            jdbcTemplate.execute("DELETE FROM driver");
            jdbcTemplate.execute("DELETE FROM geographic_point");
            jdbcTemplate.execute("DELETE FROM user");
            jdbcTemplate.execute("DELETE FROM user_role");
            return null;
        });
    }

    @Test
    @Transactional
    public void test_startVrp_repo() {
        VrpInstance savedVrpInstance = vrpInstanceRepository.save(vrpInstance);
        assertNotNull(savedVrpInstance.getId());
        assertEquals(1, vrpInstanceRepository.findAll().size());
        assertEquals(savedVrpInstance.getId(), vrpInstanceRepository.findAll().get(0).getId());
        assertEquals(vrpInstance.getUserId(), savedVrpInstance.getUserId());
        assertEquals(vrpInstance.getRoutes().size(), savedVrpInstance.getRoutes().size());
        assertEquals(vrpInstance.getDeliveryPoints().size(), savedVrpInstance.getDeliveryPoints().size());
        assertEquals(vrpInstance.getRoutes().get(0).getGeographicPointToRouteAssignments().size(), savedVrpInstance.getRoutes().get(0).getGeographicPointToRouteAssignments().size());
    }

    @Test
    @Transactional
    public void test_stopVrp_repo() {
        VrpInstance savedVrpInstance = vrpInstanceRepository.save(vrpInstance);
        assertEquals(1, vrpInstanceRepository.findAll().size());
        vrpInstanceRepository.deleteById(savedVrpInstance.getId());
        assertEquals(0, vrpInstanceRepository.findAll().size());
    }
}



