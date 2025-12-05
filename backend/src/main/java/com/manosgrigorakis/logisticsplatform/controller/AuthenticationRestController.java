package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.dto.auth.*;
import com.manosgrigorakis.logisticsplatform.dto.shared.MessageResponseDTO;
import com.manosgrigorakis.logisticsplatform.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "01. User Authentication", description = "Login, password reset and User authentication")
public class AuthenticationRestController {
    private final AuthenticationService authenticationService;

    public AuthenticationRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "User Login", description = "Authenticates user and returns JWT token")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials | Locked Account"),
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody @Valid AuthRequestDTO dto) {
        JwtResponseDTO response = authenticationService.authenticateAndGetToken(dto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Setup User Password", description = "Setup the user's password and activates their account")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Setup the user's passwords and activates their account"),
            @ApiResponse(responseCode = "400", description = "Setup token is expired"),
            @ApiResponse(responseCode = "404", description = "Setup token does not exist"),
    })
    @PostMapping("/setup-password")
    public ResponseEntity<MessageResponseDTO> setupPassword(@RequestBody @Valid SetupPasswordRequestDTO dto) {
        authenticationService.setupPassword(dto);

        return ResponseEntity.ok(new MessageResponseDTO("Your account has been successfully activated"));
    }

    @Operation(summary = "Request Reset Password", description = "Request for reset password")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Generates a URI for the user to reset their password"),
    })
    @PostMapping("/request-reset")
    public ResponseEntity<MessageResponseDTO> requestResetPassword(
            @RequestBody @Valid RequestResetPasswordRequestDTO dto) {
        authenticationService.requestResetPassword(dto);

        MessageResponseDTO response = new MessageResponseDTO(
                "If the email is registered, you will get a reset link in your email.");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validates Reset Password Token", description = "Validation of reset password token")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Validation token is valid"),
            @ApiResponse(responseCode = "400", description = "Validation token is expired"),
            @ApiResponse(responseCode = "404", description = "Validation token does not exist for the user"),
    })
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

    @Operation(summary = "Reset user's password", description = "Reset user's password")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User's password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Validation token is expired"),
            @ApiResponse(responseCode = "404", description = "Validation token does not exist for the user"),
    })
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<MessageResponseDTO> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO dto) {
        authenticationService.resetPassword(dto);

        return ResponseEntity.ok(new MessageResponseDTO("Password successfully reset"));
    }
}
