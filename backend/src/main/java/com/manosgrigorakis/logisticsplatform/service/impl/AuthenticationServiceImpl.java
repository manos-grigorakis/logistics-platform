package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.auth.AuthRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.JwtResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.RequestResetPasswordRequestDTO;
import com.manosgrigorakis.logisticsplatform.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.model.User;
import com.manosgrigorakis.logisticsplatform.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.repository.UserRepository;
import com.manosgrigorakis.logisticsplatform.repository.UserTokensRepository;
import com.manosgrigorakis.logisticsplatform.security.jwt.JwtService;
import com.manosgrigorakis.logisticsplatform.service.AuthenticationService;
import com.manosgrigorakis.logisticsplatform.utils.GenerateSecureToken;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserTokensRepository userTokensRepository;

    public AuthenticationServiceImpl(
            JwtService jwtService, AuthenticationManager authenticationManager,
            UserRepository userRepository, UserTokensRepository userTokensRepository) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userTokensRepository = userTokensRepository;
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
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);

        UserTokens userTokens = UserTokens.builder()
                .token(token)
                .type(TokenType.RESET_PASSWORD)
                .expiresAt(expirationTime)
                .build();

        userTokens.setUser(user);

        userTokensRepository.save(userTokens);

        // DEBUD:
        String resetUrl = "https://frontenddomain.com/reset-password?=" + token;
        System.out.printf(resetUrl);

        // TODO: Send reset email
    }
}
