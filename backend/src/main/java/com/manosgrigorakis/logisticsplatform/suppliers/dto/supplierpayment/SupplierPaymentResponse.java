package com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment;

import com.manosgrigorakis.logisticsplatform.suppliers.model.enums.SupplierPaymentStatus;
import com.manosgrigorakis.logisticsplatform.suppliers.model.enums.SupplierPaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SupplierPaymentResponse(
        Long id,
        String number,
        String title,
        String description,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal unpaidAmount,
        SupplierPaymentStatus status,
        SupplierPaymentType type,
        String invoiceUrl,
        String receiptUrl,
        SupplierSummaryResponse supplier,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
