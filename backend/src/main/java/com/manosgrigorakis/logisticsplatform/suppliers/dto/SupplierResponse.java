package com.manosgrigorakis.logisticsplatform.suppliers.dto;

import java.time.LocalDateTime;

public record SupplierResponse(
        Long id,
        String companyName,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
