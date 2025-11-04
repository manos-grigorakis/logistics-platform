package com.manosgrigorakis.logisticsplatform.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 30)
    private String name;
}
