package com.manosgrigorakis.logisticsplatform.mapper;

import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.model.Customer;

public class CustomerMapper {
    // DTO => Entity
    public static Customer toEntity(CustomerRequestDTO dto) {
        return Customer.builder()
                .tin(dto.getTin())
                .companyName(dto.getCompanyName())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .customerType(dto.getCustomerType())
                .location(dto.getLocation())
                .phone(dto.getPhone())
                .build();
    }

    // Entity => DTO
    public static CustomerResponseDTO toResponse(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setCompanyName(customer.getCompanyName());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setCustomerType(customer.getCustomerType());
        dto.setLocation(customer.getLocation());
        dto.setPhone(customer.getPhone());

        return dto;
    }
}
