package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.auth.AuthRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.JwtResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.RequestResetPasswordRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.ResetPasswordRequestDTO;
import com.manosgrigorakis.logisticsplatform.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.model.User;
import com.manosgrigorakis.logisticsplatform.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.repository.UserRepository;
import com.manosgrigorakis.logisticsplatform.repository.UserTokensRepository;
import com.manosgrigorakis.logisticsplatform.security.jwt.JwtService;
import com.manosgrigorakis.logisticsplatform.service.AuthenticationService;
import com.manosgrigorakis.logisticsplatform.utils.GenerateSecureToken;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserTokensRepository userTokensRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.reset_password.expires:30m}")
    private Duration expiresIn;

    public AuthenticationServiceImpl(
            JwtService jwtService, AuthenticationManager authenticationManager,
            UserRepository userRepository, UserTokensRepository userTokensRepository,
            PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userTokensRepository = userTokensRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public JwtResponseDTO authenticateAndGetToken(AuthRequestDTO dto) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );

            if (!authentication.isAuthenticated()) {
                throw new RuntimeException("Invalid credentials");
            }

            // Generate JWT
            String token = jwtService.generateToken(dto.getEmail());

            return new JwtResponseDTO(token);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication error:" + e.getMessage());
        }
    }

    @Override
    public void requestResetPassword(RequestResetPasswordRequestDTO dto) {
        // Check if user exists by email
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with email: " + dto.getEmail())
                );

        // Generate token
        String token = GenerateSecureToken.generateToken();

        // Set token expiration time
        LocalDateTime expirationTime = LocalDateTime.now().plus(expiresIn);

        UserTokens userTokens = UserTokens.builder()
                .token(token)
                .type(TokenType.RESET_PASSWORD)
                .expiresAt(expirationTime)
                .build();

        userTokens.setUser(user);

        userTokensRepository.save(userTokens);

        // TODO: Send reset email
    }

    @Override
    public void validateResetPasswordToken(String token) {
        validateResetToken(token);
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO dto) {
        UserTokens userTokens = validateResetToken(dto.getToken());

        // Update user's password
        User user = userTokens.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // Delete token (one time use)
        userTokensRepository.delete(userTokens);
    }

    // Helper method that validates the token
    private UserTokens validateResetToken(String token) {
        UserTokens userTokens = userTokensRepository.findByToken(token)
                .orElseThrow(
                        () -> new EntityNotFoundException("User Token not found with token: " + token)
                );

        if (userTokens.isExpired()) {
            throw new RuntimeException("Token is expired");
        }

        return userTokens;
    }

}
