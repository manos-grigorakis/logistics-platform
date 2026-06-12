package com.manosgrigorakis.logisticsplatform.suppliers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
        @NotBlank(message = "Company name is required")
        @Size(max = 100)
        String companyName,

        @Email(message = "Invalid email address")
        @Size(max = 320)
        String email
) {
}
