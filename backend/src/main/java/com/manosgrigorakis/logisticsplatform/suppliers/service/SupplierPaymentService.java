package com.manosgrigorakis.logisticsplatform.suppliers.service;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentUpdateRequest;

public interface SupplierPaymentService {

    SupplierPaymentResponse getSupplierPaymentById(Long id);

    SupplierPaymentResponse createSupplierPayment(SupplierPaymentRequest request);

    SupplierPaymentResponse updateSupplierPaymentById(Long id, SupplierPaymentUpdateRequest request);

    void deleteSupplierPaymentById(Long id);
}
