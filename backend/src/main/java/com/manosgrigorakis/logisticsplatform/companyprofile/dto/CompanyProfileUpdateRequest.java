package com.manosgrigorakis.logisticsplatform.companyprofile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CompanyProfileUpdateRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        @NotNull(message = "Vat percentage is required")
        Integer vatPercentage,

        @NotBlank(message = "Representative is required")
        @Size(max = 150)
        String representative,

        @NotBlank(message = "Street is required")
        @Size(max = 120)
        String street,

        @NotBlank(message = "Street number is required")
        @Size(max = 10)
        String streetNumber,

        @NotBlank(message = "Postal code is required")
        @Size(max = 10)
        String postalCode,

        @NotBlank(message = "Region is required")
        @Size(max = 100)
        String region,

        @NotBlank(message = "Country is required")
        @Size(max = 100)
        String country,

        @NotBlank(message = "Brand primary color is required")
        @Size(min = 7, max = 7)
        String brandPrimaryColor,

        @NotBlank(message = "Brand secondary color is required")
        @Size(min = 7, max = 7)
        String brandSecondaryColor,

        MultipartFile logoFile
) {
}
