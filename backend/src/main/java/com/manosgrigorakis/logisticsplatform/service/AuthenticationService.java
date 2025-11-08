package com.manosgrigorakis.logisticsplatform.service;

import com.manosgrigorakis.logisticsplatform.dto.auth.AuthRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.JwtResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.RequestResetPasswordRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.auth.ResetPasswordRequestDTO;

public interface AuthenticationService {
    JwtResponseDTO authenticateAndGetToken(AuthRequestDTO dto);

    void requestResetPassword(RequestResetPasswordRequestDTO dto);

    void validateResetPasswordToken(String token);

    void resetPassword(ResetPasswordRequestDTO dto);
}
