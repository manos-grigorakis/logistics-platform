package com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment;

import com.manosgrigorakis.logisticsplatform.suppliers.model.enums.SupplierPaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SupplierPaymentStatusUpdateRequest(
        @Schema(title = "Supplier Payment Status", description = "Supplier Payment Status", example = "paid")
        @NotNull(message = "Status is required")
        SupplierPaymentStatus status) {
}
