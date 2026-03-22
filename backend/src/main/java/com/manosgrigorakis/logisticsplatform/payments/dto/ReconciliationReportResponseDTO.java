package com.manosgrigorakis.logisticsplatform.payments.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReconciliationReportResponseDTO(
        Long id,
        String name,
        String fileUrl,
        LocalDate fromDate,
        LocalDate toDate,
        Integer matchedInvoices,
        Integer unmatchedInvoices,
        Long customerId,
        LocalDateTime createdAt
) {
}
