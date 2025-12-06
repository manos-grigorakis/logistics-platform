package com.manosgrigorakis.logisticsplatform.users.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Size(max = 320)
    @Email
    private String email;

    @Nullable
    @Size(max = 30)
    private String phone;

    @NotNull(message = "Role ID is required")
    @Positive
    private Long roleId;
}
