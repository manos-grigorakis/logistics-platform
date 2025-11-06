package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.dto.auth.AuthRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.JwtResponseDTO;
import com.manosgrigorakis.logisticsplatform.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationRestController {
    private final AuthenticationService authenticationService;

    public AuthenticationRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody @Valid AuthRequestDTO dto) {
        JwtResponseDTO response = authenticationService.authenticateAndGetToken(dto);

        return ResponseEntity.ok(response);
    }
}
