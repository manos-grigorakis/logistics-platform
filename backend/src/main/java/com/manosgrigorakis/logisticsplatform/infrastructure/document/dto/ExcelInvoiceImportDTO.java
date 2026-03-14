package com.manosgrigorakis.logisticsplatform.infrastructure.document.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExcelInvoiceImportDTO(
        String number,
        BigDecimal amount,
        LocalDate issueDate
) {}
