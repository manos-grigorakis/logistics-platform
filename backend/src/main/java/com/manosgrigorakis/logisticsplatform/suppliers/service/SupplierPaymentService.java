package com.manosgrigorakis.logisticsplatform.suppliers.service;

import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.*;
import org.springframework.data.domain.Page;

public interface SupplierPaymentService {
    Page<SupplierPaymentResponse> getSupplierPayments(SupplierPaymentFilterRequest filterRequest,
                                                      PageFilterRequest pageRequest,
                                                      SortFilterRequest sortFilterRequest);

    SupplierPaymentResponse getSupplierPaymentById(Long id);

    SupplierPaymentResponse createSupplierPayment(SupplierPaymentCreateRequest request);

    SupplierPaymentResponse updateSupplierPaymentById(Long id, SupplierPaymentUpdateRequest request);

    void updateSupplierPaymentStatusById(Long id, SupplierPaymentStatusUpdateRequest request);

    void deleteSupplierPaymentById(Long id);
}
