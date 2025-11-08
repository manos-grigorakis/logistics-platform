package com.manosgrigorakis.logisticsplatform.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {
    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "New password is required")
    private String newPassword;
}
