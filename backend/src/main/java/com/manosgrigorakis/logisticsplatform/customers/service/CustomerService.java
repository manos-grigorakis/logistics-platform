package com.manosgrigorakis.logisticsplatform.customers.service;


import com.manosgrigorakis.logisticsplatform.customers.dto.*;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {
    Page<CustomerResponseDTO> getAllCustomers(CustomerFilterRequest customerFilter, PageFilterRequest page, SortFilterRequest sort);

    CustomerResponseDTO getCustomerById(Long id);

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO updateCustomerById(Long id, UpdateCustomerRequestDTO dto);

    void deleteCustomerById(Long id);

    Page<QuoteSummaryDTO> quotesPerCustomer(PageFilterRequest page, Long id);
}
