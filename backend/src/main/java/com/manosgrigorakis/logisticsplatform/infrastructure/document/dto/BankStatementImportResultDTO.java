package com.manosgrigorakis.logisticsplatform.infrastructure.document.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BankStatementImportResultDTO (
        LocalDate date,
        BigDecimal transaction,
        String description,
        String counterPartyName
) {}
