package com.manosgrigorakis.logisticsplatform.suppliers.service;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentCreateRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentUpdateRequest;

public interface SupplierPaymentService {

    SupplierPaymentResponse getSupplierPaymentById(Long id);

    SupplierPaymentResponse createSupplierPayment(SupplierPaymentCreateRequest request);

    SupplierPaymentResponse updateSupplierPaymentById(Long id, SupplierPaymentUpdateRequest request);

    void deleteSupplierPaymentById(Long id);
}
