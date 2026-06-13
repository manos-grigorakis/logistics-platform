package com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment;

import io.swagger.v3.oas.annotations.media.Schema;

public record SupplierPaymentFilterRequest(
        @Schema(title = "Supplier Payment Number", description = "Supplier Payment Number", example = "SP-2025-0004")
        String number
) {
}
