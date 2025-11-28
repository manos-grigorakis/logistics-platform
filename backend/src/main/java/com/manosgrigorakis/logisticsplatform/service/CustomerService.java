package com.manosgrigorakis.logisticsplatform.service;


import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.CustomerResponseDTO;
import com.manosgrigorakis.logisticsplatform.dto.customer.UpdateCustomerRequestDTO;
import com.manosgrigorakis.logisticsplatform.filters.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.filters.SortFilterRequest;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Page<CustomerResponseDTO> getAllCustomers(PageFilterRequest page, SortFilterRequest sort);

    CustomerResponseDTO getCustomerById(Long id);

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO updateCustomerById(Long id, UpdateCustomerRequestDTO dto);

    void deleteCustomerById(Long id);
}
