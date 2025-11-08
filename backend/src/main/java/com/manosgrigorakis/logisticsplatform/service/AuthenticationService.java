package com.manosgrigorakis.logisticsplatform.service;

import com.manosgrigorakis.logisticsplatform.dto.auth.*;

public interface AuthenticationService {
    JwtResponseDTO authenticateAndGetToken(AuthRequestDTO dto);

    void setupPassword(SetupPasswordRequestDTO dto);

    void requestResetPassword(RequestResetPasswordRequestDTO dto);

    void validateResetPasswordToken(String token);

    void resetPassword(ResetPasswordRequestDTO dto);
}
