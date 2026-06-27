package com.manosgrigorakis.logisticsplatform.customers.mapper;

import com.manosgrigorakis.logisticsplatform.customers.dto.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.customers.dto.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.customers.dto.UpdateCustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toEntity(CustomerRequestDTO dto);

    void toUpdate(@MappingTarget Customer customer, UpdateCustomerRequestDTO dto);

    CustomerResponseDTO toResponse(Customer customer);
}
