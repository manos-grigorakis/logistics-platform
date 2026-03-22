package com.manosgrigorakis.logisticsplatform.payments.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReconciliationReportCreateResponseDTO(
        Long id,
        String name,
        String fileUrl,
        LocalDate fromDate,
        LocalDate toDate,
        Long customerId,
        LocalDateTime createdAt
) {}
