package com.manosgrigorakis.logisticsplatform.common.web;

import com.manosgrigorakis.logisticsplatform.auth.dto.AuthRequestDTO;
import com.manosgrigorakis.logisticsplatform.auth.dto.JwtResponseDTO;
import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class HttpRequestTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    protected JavaMailSender javaMailSender;

    protected String BASE_URL;

    protected String authToken;

    /**
     * Authenticates a {@link User} using the credentials the provided credentials
     * @return The JWT token from the response
     */
    protected String authenticate(String email, String password) {
        AuthRequestDTO request = new AuthRequestDTO(email, password);
        ResponseEntity<JwtResponseDTO> response = restTemplate.postForEntity(
                BASE_URL + "/auth/login", request, JwtResponseDTO.class
        );

        Assertions.assertNotNull(response.getBody());
        return response.getBody().getToken();
    }

    /**
     * Sets Bearer Authorization in {@link #authToken}
     * @return The headers with the token
     */
    protected HttpHeaders setAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return headers;
    }
}
