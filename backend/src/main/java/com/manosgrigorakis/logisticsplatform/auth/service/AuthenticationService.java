package com.manosgrigorakis.logisticsplatform.auth.service;

import com.manosgrigorakis.logisticsplatform.auth.dto.*;

public interface AuthenticationService {
    JwtResponseDTO authenticateAndGetToken(AuthRequestDTO dto);

    void setupPassword(SetupPasswordRequestDTO dto);

    void requestResetPassword(RequestResetPasswordRequestDTO dto);

    void validateResetPasswordToken(String token);

    void resetPassword(ResetPasswordRequestDTO dto);
}
