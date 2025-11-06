package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.dto.auth.AuthRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.JwtResponseDTO;
import com.manosgrigorakis.logisticsplatform.security.jwt.JwtService;
import com.manosgrigorakis.logisticsplatform.service.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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
}
