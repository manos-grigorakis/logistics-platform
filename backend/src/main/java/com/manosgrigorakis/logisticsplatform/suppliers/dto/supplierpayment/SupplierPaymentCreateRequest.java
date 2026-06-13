package com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment;

import com.manosgrigorakis.logisticsplatform.suppliers.model.enums.SupplierPaymentType;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record SupplierPaymentCreateRequest(
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

        MultipartFile invoiceFile,

        MultipartFile receiptFile,

        @NotNull(message = "Supplier ID is required")
        @Positive(message = "Supplier ID must be positive")
        Long supplierId

) {
}
