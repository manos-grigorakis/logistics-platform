package com.manosgrigorakis.logisticsplatform.suppliers.service;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentResponse;

public interface SupplierPaymentService {

    SupplierPaymentResponse getSupplierPaymentById(Long id);

    SupplierPaymentResponse createSupplierPayment(SupplierPaymentRequest request);

    SupplierPaymentResponse updateSupplierPaymentById(Long id, SupplierPaymentRequest request);

    void deleteSupplierPaymentById(Long id);
}
