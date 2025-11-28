package com.manosgrigorakis.logisticsplatform.service;


import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.model.Customer;

import java.util.List;

public interface CustomerService {
    List<CustomerResponseDTO> getAllCustomers();

    CustomerResponseDTO getCustomerById(Long id);

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO updateCustomerById(Long id, CustomerRequestDTO dto);

    void deleteCustomerById(Long id);
}
