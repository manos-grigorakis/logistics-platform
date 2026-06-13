package com.manosgrigorakis.logisticsplatform.suppliers.service;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentResponse;

public interface SupplierPaymentService {
    SupplierPaymentResponse createSupplierPayment(SupplierPaymentRequest request);
}
