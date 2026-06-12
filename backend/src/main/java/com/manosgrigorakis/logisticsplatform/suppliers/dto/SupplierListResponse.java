package com.manosgrigorakis.logisticsplatform.suppliers.dto;

import java.math.BigDecimal;

public record SupplierListResponse(Long id, String companyName, BigDecimal totalAmount, BigDecimal remainingAmount) {
}
