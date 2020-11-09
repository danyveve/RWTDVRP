package com.marcu.vrp;

import com.marcu.vrp.backend.model.User;
import com.marcu.vrp.backend.model.UserRole;
import com.marcu.vrp.backend.model.UserRoleName;
import com.marcu.vrp.backend.repository.UserRepository;
import com.marcu.vrp.backend.repository.UserRoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityTests {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

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
    public void noAuthPublicRouteAccess() throws Exception {
        mvc.perform(get("/test/public").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void noAuthPrivateRouteAccess() throws Exception {
        mvc.perform(get("/test/private").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(value = "spring")
    public void withAuthPublicRouteAccess() throws Exception {
        HashSet<Double> doubles = new HashSet<>();
        int i = doubles.hashCode();
        doubles.add(11d);
        int ii = doubles.hashCode();
        doubles.clear();
        doubles.add(11.00001);
        int iii = doubles.hashCode();
        mvc.perform(get("/test/public").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(value = "spring")
    public void withAuthPrivateRouteAccess() throws Exception {
        mvc.perform(get("/test/private").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void testLoginSuccess() throws Exception {
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

        mvc.perform(post("/login")
            .param("username", "test-user")
            .param("password", "test")
        ).andExpect(status().isOk());
    }

    @Test
    public void testLoginError() throws Exception {
        mvc.perform(post("/login")
            .param("username", "fake")
            .param("password", "fake")
        ).andExpect(status().isUnauthorized());
    }
}
