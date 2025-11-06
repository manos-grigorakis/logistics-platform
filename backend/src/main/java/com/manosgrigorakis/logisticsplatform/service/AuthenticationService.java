package com.manosgrigorakis.logisticsplatform.service;

import com.manosgrigorakis.logisticsplatform.dto.auth.AuthRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.JwtResponseDTO;

public interface AuthenticationService {
    JwtResponseDTO authenticateAndGetToken(AuthRequestDTO dto);
}
