package com.manosgrigorakis.logisticsplatform.suppliers.dto;

import com.manosgrigorakis.logisticsplatform.suppliers.model.enums.SupplierPaymentType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SupplierPaymentRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 50)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull(message = "Total amount is required")
        @DecimalMin("0.00")
        @Digits(integer = 19, fraction = 2)
        BigDecimal totalAmount,

        @DecimalMin("0.00")
        @Digits(integer = 19, fraction = 2)
        BigDecimal paidAmount,

        @NotNull(message = "Type is required")
        SupplierPaymentType type,

        @NotNull(message = "Supplier ID is required")
        @Positive(message = "Supplier ID must be positive")
        Long supplierId

) {
}
