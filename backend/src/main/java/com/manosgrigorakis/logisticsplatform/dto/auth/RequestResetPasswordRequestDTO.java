package com.manosgrigorakis.logisticsplatform.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestResetPasswordRequestDTO {
    @NotBlank(message = "Email is required")
    @Email
    private String email;
}
