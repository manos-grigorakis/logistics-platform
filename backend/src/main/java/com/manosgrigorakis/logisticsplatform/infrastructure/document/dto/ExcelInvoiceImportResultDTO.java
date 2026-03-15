package com.manosgrigorakis.logisticsplatform.infrastructure.document.dto;

import java.util.List;

public record ExcelInvoiceImportResultDTO(
        String tin,
        List<ExcelInvoiceImportDTO> invoices
) {
}
