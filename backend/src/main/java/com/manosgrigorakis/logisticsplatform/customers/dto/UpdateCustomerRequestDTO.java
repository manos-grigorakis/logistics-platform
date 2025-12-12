package com.manosgrigorakis.logisticsplatform.customers.dto;

import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerRequestDTO {
    @NotBlank(message = "Company name is required")
    @Size(max = 80)
    private String companyName;

    @NotBlank(message = "First name is required")
    @Size(max = 80)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 80)
    private String lastName;

    @Size(max = 320)
    @Email
    private String email;

    @NotNull(message = "Customer type is required (Individual | Company)")
    private CustomerType customerType;

    @Nullable
    @Size(max = 255)
    private String location;

    @Nullable
    @Size(max = 30)
    private String phone;
}
