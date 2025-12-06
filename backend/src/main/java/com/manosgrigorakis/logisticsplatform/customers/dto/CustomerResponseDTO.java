package com.manosgrigorakis.logisticsplatform.customers.dto;

import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerResponseDTO {
    private Long id;
    private String tin;
    private String companyName;
    private String firstName;
    private String lastName;
    private String email;
    private CustomerType customerType;
    private String location;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
