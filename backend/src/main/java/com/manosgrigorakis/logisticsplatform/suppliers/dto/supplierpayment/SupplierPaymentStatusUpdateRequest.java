package com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment;

import com.manosgrigorakis.logisticsplatform.suppliers.model.enums.SupplierPaymentStatus;
import jakarta.validation.constraints.NotNull;

public record SupplierPaymentStatusUpdateRequest(
        @NotNull(message = "Status is required")
        SupplierPaymentStatus status) {
}
