package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.auth.*;
import com.manosgrigorakis.logisticsplatform.enums.TokenType;
import com.manosgrigorakis.logisticsplatform.enums.UserStatus;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.exception.TokenExpiredException;
import com.manosgrigorakis.logisticsplatform.model.User;
import com.manosgrigorakis.logisticsplatform.model.UserTokens;
import com.manosgrigorakis.logisticsplatform.repository.UserRepository;
import com.manosgrigorakis.logisticsplatform.repository.UserTokensRepository;
import com.manosgrigorakis.logisticsplatform.security.jwt.JwtService;
import com.manosgrigorakis.logisticsplatform.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserTokensRepository userTokensRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final UserTokensServiceImpl userTokensService;

    private final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

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
            log.info("Authenticating user with email {}", dto.getEmail());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );

            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String roleName = user.getRole().getName();

            // Generate JWT
            String token = jwtService.generateToken(dto.getEmail(), roleName);
            log.info("User {} authenticated successfully", dto.getEmail());

            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole().getName()
            );

            return new JwtResponseDTO("Bearer",token, userDetailsDTO);
        } catch (BadCredentialsException e) {
            log.error("Authentication error for user {}: bad credentials", dto.getEmail());
            throw e;
        } catch (DisabledException e) {
            log.error("Authentication error for user {}: account disabled", dto.getEmail());
            throw e;
        } catch (LockedException e) {
            log.error("Authentication error for user {}: account locked", dto.getEmail());
            throw e;
        }
    }

    @Override
    public void setupPassword(SetupPasswordRequestDTO dto) {
        log.info("Starting password setup");
        UserTokens userTokens =  validateToken(dto.getToken());

        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        User user = userTokens.getUser();
        user.setPassword(hashedPassword);
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        userRepository.save(user);

        log.info("Account activated successfully for user {}", user.getEmail());

        // Delete associated token
        userTokensRepository.delete(userTokens);
        log.info("Password setup token deleted for user {}", user.getEmail());
    }

    @Override
    public void requestResetPassword(RequestResetPasswordRequestDTO dto) {
        log.info("Password reset request for user with email {}", dto.getEmail());

        // Check if user exists by email
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());

        if(optionalUser.isEmpty()) {
            log.warn("Password reset failed. User not found with email {}", dto.getEmail());
            return;
        }

        User user = optionalUser.get();

        // Generate token
        UserTokens userTokens = userTokensService.
                generateUserTokens(TokenType.RESET_PASSWORD, resetPasswordTokenExpiresIn, user);

        mailService.sendResetPasswordEmail(user.getFirstName(),user.getEmail(), userTokens.getToken());
        log.info("Reset password email sent for user {}", user.getEmail());
    }

    @Override
    public void validateResetPasswordToken(String token) {
        log.info("Validating reset password token");
        validateToken(token);
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO dto) {
        log.info("Password reset");
        UserTokens userTokens = validateToken(dto.getToken());

        // Update user's password
        User user = userTokens.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // Delete token (one time use)
        userTokensRepository.delete(userTokens);
        log.info("Password reset completed and token deleted for user {}", user.getEmail());
    }

    // Helper method that validates the token
    private UserTokens validateToken(String token) {
        log.info("Validate user token");
        UserTokens userTokens = userTokensRepository.findByToken(token)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User Token not found with token: " + token)
                );

        if (userTokens.isExpired()) {
            log.warn("Expired token detected for user {}", userTokens.getUser().getEmail());
            throw new TokenExpiredException();
        }

        return userTokens;
    }
}
