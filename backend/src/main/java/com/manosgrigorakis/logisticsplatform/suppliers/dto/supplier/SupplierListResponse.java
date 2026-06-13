package com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier;

import java.math.BigDecimal;

public record SupplierListResponse(
        Long id,
        String companyName,
        String email,
        boolean isActive,
        BigDecimal totalAmount,
        BigDecimal remainingAmount
) {
}
