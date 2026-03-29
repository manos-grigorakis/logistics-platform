package com.manosgrigorakis.logisticsplatform.auth;

import com.manosgrigorakis.logisticsplatform.auth.dto.AuthRequestDTO;
import com.manosgrigorakis.logisticsplatform.auth.dto.JwtResponseDTO;
import com.manosgrigorakis.logisticsplatform.common.web.HttpRequestTest;
import com.manosgrigorakis.logisticsplatform.users.enums.UserStatus;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.users.repository.RoleRepository;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest extends HttpRequestTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String AUTH_URL;
    private User registeredUser;
    private String token;

    private static final String RAW_PASSWORD = "admin";
    private static final String EMAIL = "jdoe@logistics.com";

    @BeforeAll
    void beforeAll() {
        BASE_URL = "http://localhost:%d/api".formatted(port);
        AUTH_URL = BASE_URL + "/auth";
        registeredUser = registerUserWithRole();
        token = authenticate();
    }

    @Test
    public void login() {
        // Arrange
        String url = AUTH_URL + "/login";
        AuthRequestDTO authRequestDTO = new AuthRequestDTO(EMAIL, RAW_PASSWORD);

        // Act
        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(authRequestDTO), JwtResponseDTO.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getUser().getEmail()).isEqualTo(EMAIL);
        assertThat(response.getBody().getUser().getRole()).isEqualTo("ADMIN");
    }

    @Test
    public void loginWithWrongCredentials_shouldReturn401() {
        // Arrange
        String url = AUTH_URL + "/login";
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("adoe@logistics.com", "admin123");

        // Act
        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(authRequestDTO), JwtResponseDTO.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    public void loginWithMissingEmail_shouldReturn400() {
        // Arrange
        String url = AUTH_URL + "/login";
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("", "admin123");

        // Act
        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(authRequestDTO), JwtResponseDTO.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    /**
     * Creates and saves in the database an active {@link User} using {@link #EMAIL} & {@link #RAW_PASSWORD}
     * and assigns the role using {@link #createRole()}
     * @return The created {@link User}
     */
    private User registerUserWithRole() {
        Role role = createRole();

        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(EMAIL)
                .status(UserStatus.ACTIVE)
                .build();

        user.setPassword(passwordEncoder.encode(RAW_PASSWORD));
        user.setEnabled(true);
        user.setRole(role);

        userRepository.save(user);
        return user;
    }

    /**
     * Creates an {@link UserStatus#INVITED} {@link User} using {@link #EMAIL}
     * and assigns the role using {@link #createRole()}
     * @return The created {@link User}
     */
    private User createInvitedUser() {
        Role role = createRole();

        User user = User.builder()
                .firstName("A")
                .lastName("Doe")
                .email("adoe@logistics.com")
                .status(UserStatus.INVITED)
                .build();

        user.setRole(role);
        userRepository.save(user);
        return user;
    }

    /**
     * Creates and saves a {@link Role} in the database
     * @return The created {@link Role}
     */
    private Role createRole() {
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);
        return role;
    }

    /**
     * Authenticates a {@link User} using the credentials:
     * <ul>
     *     <li>Email: {@link #EMAIL}</li>
     *     <li>Password: {@link #RAW_PASSWORD}</li>
     * </ul>
     * @return The JWT token from the response
     */
    private String authenticate() {
        AuthRequestDTO request = new AuthRequestDTO(EMAIL, RAW_PASSWORD);
        ResponseEntity<JwtResponseDTO> response = restTemplate.postForEntity(
                AUTH_URL + "/login", request, JwtResponseDTO.class
        );

        Assertions.assertNotNull(response.getBody());
        return response.getBody().getToken();
    }

    /**
     * Sets Bearer Authorization in {@link #token}
     * @return The headers with the token
     */
    private HttpHeaders setAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}
