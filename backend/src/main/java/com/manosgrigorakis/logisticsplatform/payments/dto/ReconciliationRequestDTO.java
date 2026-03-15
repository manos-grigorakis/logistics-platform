package com.manosgrigorakis.logisticsplatform.payments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ReconciliationRequestDTO {
    @Schema(description = "Customer's ID")
    @NotNull(message = "Customer id is required")
    private Long customerId;

    @Schema(type = "string", format = "binary", description = "Invoices Excel file")
    @NotNull(message = "Invoices Excel file is required")
    private MultipartFile invoiceFile;

    @Schema(type = "string", format = "binary", description = "Bank statement Excel file")
    @NotNull(message = "Bank statement Excel file file is required")
    private MultipartFile bankStatement;
}
