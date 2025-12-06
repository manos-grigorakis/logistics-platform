package com.manosgrigorakis.logisticsplatform.customers.mapper;

import com.manosgrigorakis.logisticsplatform.customers.dto.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.customers.dto.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;

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

    // Entity => Response
    public static CustomerResponseDTO toResponse(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setTin(customer.getTin());
        dto.setCompanyName(customer.getCompanyName());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setCustomerType(customer.getCustomerType());
        dto.setLocation(customer.getLocation());
        dto.setPhone(customer.getPhone());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());

        return dto;
    }
}
