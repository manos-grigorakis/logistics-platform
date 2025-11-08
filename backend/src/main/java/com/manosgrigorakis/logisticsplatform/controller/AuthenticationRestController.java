package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.dto.auth.*;
import com.manosgrigorakis.logisticsplatform.dto.shared.MessageResponseDTO;
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

    @PostMapping("/setup-password")
    public ResponseEntity<MessageResponseDTO> setupPassword(@RequestBody @Valid SetupPasswordRequestDTO dto) {
        authenticationService.setupPassword(dto);

        return ResponseEntity.ok(new MessageResponseDTO("Your account has been successfully activated"));
    }

    @PostMapping("/request-reset")
    public ResponseEntity<MessageResponseDTO> requestResetPassword(
            @RequestBody @Valid RequestResetPasswordRequestDTO dto) {
        authenticationService.requestResetPassword(dto);

        MessageResponseDTO response = new MessageResponseDTO(
                "If they email is registered, you will get a reset link in your email.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ValidateResetPasswordTokenResponseDTO> validateResetPasswordToken(
            @RequestParam("token") String token) {
        authenticationService.validateResetPasswordToken(token);

        ValidateResetPasswordTokenResponseDTO response = new ValidateResetPasswordTokenResponseDTO(
                true,
                "Token is valid."
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<MessageResponseDTO> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO dto) {
        authenticationService.resetPassword(dto);

        return ResponseEntity.ok(new MessageResponseDTO("Password successfully reset"));
    }
}
