package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.auth.*;
import com.manosgrigorakis.logisticsplatform.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.enums.UserStatus;
import com.manosgrigorakis.logisticsplatform.model.User;
import com.manosgrigorakis.logisticsplatform.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.repository.UserRepository;
import com.manosgrigorakis.logisticsplatform.repository.UserTokensRepository;
import com.manosgrigorakis.logisticsplatform.security.jwt.JwtService;
import com.manosgrigorakis.logisticsplatform.service.AuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserTokensRepository userTokensRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final UserTokensServiceImpl userTokensService;

    @Value("${app.reset_password.expires:30m}")
    private Duration resetPasswordTokenExpiresIn;

    public AuthenticationServiceImpl(
            JwtService jwtService, AuthenticationManager authenticationManager,
            UserRepository userRepository, UserTokensRepository userTokensRepository,
            PasswordEncoder passwordEncoder, MailService mailService,
            UserTokensServiceImpl userTokensService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userTokensRepository = userTokensRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.userTokensService = userTokensService;
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
    public void setupPassword(SetupPasswordRequestDTO dto) {
        UserTokens userTokens =  validateToken(dto.getToken());

        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        User user = userTokens.getUser();
        user.setPassword(hashedPassword);
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        userRepository.save(user);

        // Delete associated token
        userTokensRepository.delete(userTokens);
    }

    @Override
    public void requestResetPassword(RequestResetPasswordRequestDTO dto) {
        // Check if user exists by email
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with email: " + dto.getEmail())
                );

        // Generate token
        UserTokens userTokens = userTokensService.
                generateUserTokens(TokenType.RESET_PASSWORD, resetPasswordTokenExpiresIn, user);

        mailService.sendResetPasswordEmail(user.getFirstName(),user.getEmail(), userTokens.getToken());
    }

    @Override
    public void validateResetPasswordToken(String token) {
        validateToken(token);
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO dto) {
        UserTokens userTokens = validateToken(dto.getToken());

        // Update user's password
        User user = userTokens.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // Delete token (one time use)
        userTokensRepository.delete(userTokens);
    }

    // Helper method that validates the token
    private UserTokens validateToken(String token) {
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
