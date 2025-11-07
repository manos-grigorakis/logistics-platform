package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.dto.auth.*;
import com.manosgrigorakis.logisticsplatform.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/request-reset")
    public ResponseEntity<RequestResetPasswordResponseDTO> requestResetPassword(
            @RequestBody @Valid RequestResetPasswordRequestDTO dto) {
        authenticationService.requestResetPassword(dto);

        RequestResetPasswordResponseDTO response = new RequestResetPasswordResponseDTO(
                "If they email is registered, you will get a reset link in your email.");

        return ResponseEntity.ok(response);
    }
}
