package com.manosgrigorakis.logisticsplatform.auth;

import com.manosgrigorakis.logisticsplatform.auth.dto.*;
import com.manosgrigorakis.logisticsplatform.auth.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.auth.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.auth.repository.UserTokensRepository;
import com.manosgrigorakis.logisticsplatform.common.dto.MessageResponseDTO;
import com.manosgrigorakis.logisticsplatform.common.web.HttpRequestTest;
import com.manosgrigorakis.logisticsplatform.users.enums.UserStatus;
import com.manosgrigorakis.logisticsplatform.users.model.Role;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import com.manosgrigorakis.logisticsplatform.users.repository.RoleRepository;
import com.manosgrigorakis.logisticsplatform.users.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest extends HttpRequestTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserTokensRepository userTokensRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String AUTH_URL;

    private static final String RAW_PASSWORD = "admin";
    private static final String EMAIL = "jdoe@logistics.com";

    @BeforeAll
    void beforeAll() {
        BASE_URL = "http://localhost:%d/api".formatted(port);
        AUTH_URL = BASE_URL + "/auth";
        authToken = authenticate(EMAIL, RAW_PASSWORD);
    }

    @BeforeEach
    public void beforeEach() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        registerUserWithRole();
    }

    @AfterEach
    public void afterEach() {
        userTokensRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
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

    @Test
    public void loginWithInvitedUser_shouldReturn401() {
        // Arrange
        String url = AUTH_URL + "/login";
        User user = createInvitedUser();
        AuthRequestDTO authRequestDTO = new AuthRequestDTO(user.getEmail(), "admin123");

        // Act
        ResponseEntity<JwtResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(authRequestDTO), JwtResponseDTO.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    public void requestResetPassword() {
        // Arrange & Act
        TestResetPasswordResult resetPasswordResult = getRequestResetToken();

        // Assert
        assertThat(resetPasswordResult.response().getStatusCode().value()).isEqualTo(200);
        assertThat(resetPasswordResult.userTokens().getType()).isEqualTo(TokenType.RESET_PASSWORD);
    }

    @Test
    public void resetPassword() {
        // Arrange & Act for Reset Token
        TestResetPasswordResult resetPasswordResult = getRequestResetToken();

        // Arrange
        ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO();
        resetPasswordRequestDTO.setToken(resetPasswordResult.userTokens().getToken());
        resetPasswordRequestDTO.setNewPassword("admin123");

        // Act
        ResponseEntity<MessageResponseDTO> response = restTemplate.postForEntity(
                AUTH_URL + "/reset-password/confirm", resetPasswordRequestDTO, MessageResponseDTO.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    public void resetPassword_withInvalidToken_shouldReturn404() {
        // Arrange
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setToken(UUID.randomUUID().toString());
        request.setNewPassword("admin123");

        // Act
        ResponseEntity<MessageResponseDTO> response = restTemplate.postForEntity(
                AUTH_URL + "/reset-password/confirm", request, MessageResponseDTO.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    public void setupPassword() {
        // Arrange & Act for Setup Token
        UserTokens setupToken = getSetupToken();

        // Arrange
        SetupPasswordRequestDTO requestDTO = new SetupPasswordRequestDTO();
        requestDTO.setToken(setupToken.getToken());
        requestDTO.setPassword(RAW_PASSWORD);

        // Act
        ResponseEntity<MessageResponseDTO> response = restTemplate.postForEntity(
                AUTH_URL + "/setup-password", requestDTO, MessageResponseDTO.class);

        User user = userRepository.findByEmail(setupToken.getUser().getEmail())
                .orElseThrow(() -> new AssertionError("User not found for setup password"));

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getEnabled()).isEqualTo(true);
    }

    /**
     * Creates and saves in the database an active {@link User} using {@link #EMAIL} & {@link #RAW_PASSWORD}
     * and assigns the role using {@link #createRole()}
     */
    private void registerUserWithRole() {
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
     * Searches for a role with name {@code ADMIN}, if not exist it creates and saves a {@link Role} in the database
     * @return The created {@link Role}
     */
    private Role createRole() {
        return roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });
    }

    /**
     * Performs a request to reset password using the {@link #createInvitedUser()}
     * @return The response from the request and the {@link UserTokens}
     * @throw {@link AssertionError} if token doesn't exist by user id
     */
    private TestResetPasswordResult getRequestResetToken() {
        // Arrange
        String url = AUTH_URL + "/request-reset";
        User user = createInvitedUser();
        RequestResetPasswordRequestDTO dto = new RequestResetPasswordRequestDTO();
        dto.setEmail(user.getEmail());

        // Act
        ResponseEntity<MessageResponseDTO> response = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(dto), MessageResponseDTO.class);

        UserTokens token = userTokensRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AssertionError("Token not found for user"));

        return new TestResetPasswordResult(response, token);
    }

    /**
     * Creates an {@link UserStatus#INVITED} user using {@link #createInvitedUser()},
     * and a new {@link UserTokens} of type {@link TokenType#CREATE_PASSWORD}
     * @return The created {@link UserTokens}
     * @throw {@link AssertionError} if token was not found for the user
     */
    private UserTokens getSetupToken() {
        User user = createInvitedUser();

        UserTokens setupToken = new UserTokens();
        setupToken.setUser(user);
        setupToken.setToken(UUID.randomUUID().toString());
        setupToken.setType(TokenType.CREATE_PASSWORD);
        setupToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        userTokensRepository.save(setupToken);

        return userTokensRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AssertionError("Setup Token not found for user"));
    }
}
