package com.manosgrigorakis.logisticsplatform.customers.service;


import com.manosgrigorakis.logisticsplatform.customers.dto.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.customers.dto.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.customers.dto.UpdateCustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.customers.dto.CustomerFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.SortFilterRequest;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Page<CustomerResponseDTO> getAllCustomers(CustomerFilterRequest customerFilter, PageFilterRequest page, SortFilterRequest sort);

    CustomerResponseDTO getCustomerById(Long id);

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO updateCustomerById(Long id, UpdateCustomerRequestDTO dto);

    void deleteCustomerById(Long id);
}
