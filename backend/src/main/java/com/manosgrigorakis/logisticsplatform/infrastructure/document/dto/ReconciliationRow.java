package com.manosgrigorakis.logisticsplatform.infrastructure.document.dto;


import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;


public record ReconciliationRow(
        LocalDate invoiceIssueDate,
        String invoiceNumber,
        InvoiceStatus invoiceStatus,
        BigDecimal invoiceAmount,
        String bankName,
        LocalDate depositDate,
        BigDecimal depositAmount,
        BigDecimal remainingAmount
) {}
