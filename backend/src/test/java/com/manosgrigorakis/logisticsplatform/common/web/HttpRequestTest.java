package com.manosgrigorakis.logisticsplatform.common.web;

import com.manosgrigorakis.logisticsplatform.infrastructure.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class HttpRequestTest {
    @LocalServerPort
    private int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private JavaMailSender javaMailSender;

    protected String BASE_URL;

    @BeforeEach
    public void setUp() {
        BASE_URL = "http://localhost:%d/api".formatted(port);
    }
}
