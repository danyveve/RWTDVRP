package com.marcu.vrp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marcu.vrp.backend.adapter.VrpToProviderAdapter;
import com.marcu.vrp.backend.dto.DriverDTO;
import com.marcu.vrp.backend.dto.GeographicPointDTO;
import com.marcu.vrp.backend.dto.RWTDVRPSolution;
import com.marcu.vrp.backend.dto.StartVrpSolverRequestDTO;
import com.marcu.vrp.backend.model.Driver;
import com.marcu.vrp.backend.model.User;
import com.marcu.vrp.backend.model.UserRole;
import com.marcu.vrp.backend.model.UserRoleName;
import com.marcu.vrp.backend.repository.DriverRepository;
import com.marcu.vrp.backend.repository.GeographicPointRepository;
import com.marcu.vrp.backend.repository.UserRepository;
import com.marcu.vrp.backend.repository.UserRoleRepository;
import com.marcu.vrp.backend.repository.VrpInstanceRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StartStopIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private VrpToProviderAdapter<String, RWTDVRPSolution> vrpToProviderAdapter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private GeographicPointRepository geographicPointRepository;

    @Autowired
    private VrpInstanceRepository vrpInstanceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;


    private StartVrpSolverRequestDTO startVrpSolverRequestDTO;

    @BeforeAll
    public void setup() {
        Driver d1 = Driver.builder().firstName("firstName1").lastName("lastName1").phone("+0111").email("email1@gmail.com").car("car1").build();
        Driver d2 = Driver.builder().firstName("firstName2").lastName("lastName2").phone("+0222").email("email2@gmail.com").car("car2").build();
        Driver d3 = Driver.builder().firstName("firstName3").lastName("lastName3").phone("+0333").email("email3@gmail.com").car("car3").build();
        Long d1Id = driverRepository.save(d1).getId();
        Long d2Id = driverRepository.save(d2).getId();
        Long d3Id = driverRepository.save(d3).getId();


        startVrpSolverRequestDTO = StartVrpSolverRequestDTO.builder().build();
        GeographicPointDTO depot = GeographicPointDTO.builder().latitude(0D).longitude(0D).address("address1").build();
        startVrpSolverRequestDTO.setDepot(depot);
        Instant preferredDeparture = Instant.now();
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
        DriverDTO driver1 = DriverDTO.builder().firstName("firstName1").lastName("lastName1").phone("+0111").email("email1@gmail.com").car("car1").build();
        DriverDTO driver2 = DriverDTO.builder().firstName("firstName2").lastName("lastName2").phone("+0222").email("email2@gmail.com").car("car2").build();
        DriverDTO driver3 = DriverDTO.builder().firstName("firstName3").lastName("lastName3").phone("+0333").email("email3@gmail.com").car("car3").build();
        driver1.setId(d1Id);
        driver2.setId(d2Id);
        driver3.setId(d3Id);
        driverDTOS.add(driver1);
        driverDTOS.add(driver2);
        driverDTOS.add(driver3);
        startVrpSolverRequestDTO.setDrivers(driverDTOS);
    }

    @AfterAll
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
    @Order(1)
    @WithMockUser(value = "test-user")
    public void integration_startVrp_test() throws Exception {
        UserRole userRole = UserRole.builder()
                .name(UserRoleName.CLIENT)
                .build();
        Long userRoleId = userRoleRepository.save(userRole).getId();

        User user = User.builder()
                .username("test-user")
                .password(new BCryptPasswordEncoder().encode("test"))
                .email("test@gmail.com")
                .firstName("").lastName("").phone("")
                .roleId(userRoleId)
                .build();
        userRepository.save(user);

        given(vrpToProviderAdapter.doVrpRequest(any())).willReturn("1");

        mvc.perform(post("/api/vrp/start")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(asJsonString(startVrpSolverRequestDTO))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));

        assertEquals(4, geographicPointRepository.findAll().size());
        assertEquals(1, vrpInstanceRepository.findAll().size());
        assertEquals(3, vrpInstanceRepository.findAll().get(0).getRoutes().size());
        assertEquals(3, vrpInstanceRepository.findAll().get(0).getDeliveryPoints().size());
    }

    @Test
    @Order(2)
    @WithMockUser(value = "test-user")
    public void integration_stopVrp_test() throws Exception {
        assertEquals(1, vrpInstanceRepository.findAll().size());

        RWTDVRPSolution rwtdvrpSolution = RWTDVRPSolution.builder().departureTime(Instant.now()).totalCost(100L).routes(new ArrayList<>()).build();
        given(vrpToProviderAdapter.stopVrpRequest(any())).willReturn(rwtdvrpSolution);

        Long id = vrpInstanceRepository.findAll().get(0).getId();
        mvc.perform(get("/api/vrp/stop/" + id.toString()))
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(status().isOk());
        assertEquals(1, vrpInstanceRepository.findAll().size());
        assertEquals(100L, vrpInstanceRepository.findAll().get(0).getTotalCost());
    }

    static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();
            mapper.registerModule(module);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
