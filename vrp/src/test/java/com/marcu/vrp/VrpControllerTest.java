package com.marcu.vrp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marcu.vrp.backend.dto.DriverDTO;
import com.marcu.vrp.backend.dto.GeographicPointDTO;
import com.marcu.vrp.backend.dto.StartVrpSolverRequestDTO;
import com.marcu.vrp.backend.dto.UserDTO;
import com.marcu.vrp.backend.dto.VrpInstanceDTO;
import com.marcu.vrp.backend.service.UserService;
import com.marcu.vrp.backend.service.VrpService;
import com.marcu.vrp.frontend.controller.VrpController;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {VrpController.class})
@ActiveProfiles("test")
class VrpControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VrpService vrpService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(value = "spring")
    void test_startVrp_controller() throws Exception {
        StartVrpSolverRequestDTO startVrpSolverRequestDTO = StartVrpSolverRequestDTO.builder().build();
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
        driverDTOS.add(driver1);
        driverDTOS.add(driver2);
        driverDTOS.add(driver3);
        startVrpSolverRequestDTO.setDrivers(driverDTOS);

        UserDTO mockUser = UserDTO.builder().build();
        mockUser.setId(1L);

        given(userService.loadUserByUsername(ArgumentMatchers.anyString())).willReturn(mockUser);
        given(vrpService.startVrp(any())).willReturn(1L);

        mvc.perform(post("/api/vrp/start")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(asJsonString(startVrpSolverRequestDTO))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }

    @Test
    @WithMockUser(value = "spring")
    void test_stopVrp_controller() throws Exception {
        VrpInstanceDTO vrpInstanceDTO = VrpInstanceDTO.builder().build();
        vrpInstanceDTO.setId(1L);

        given(vrpService.stopVrp(1L)).willReturn(vrpInstanceDTO);
        mvc.perform(get("/api/vrp/stop/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(status().isOk());
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
