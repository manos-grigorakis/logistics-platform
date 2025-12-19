package com.manosgrigorakis.logisticsplatform.customers.dto;

import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record QuoteSummaryDTO(
        Long id,
        String number,
        BigDecimal grossPrice,
        QuoteStatus status,
        LocalDate issueDate)
{}
