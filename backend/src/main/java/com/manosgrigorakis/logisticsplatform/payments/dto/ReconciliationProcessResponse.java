package com.manosgrigorakis.logisticsplatform.payments.dto;


public record ReconciliationProcessResponse (
        Integer totalInvoices,
        Integer matchedInvoices,
        Integer unmatchedInvoices,
        Integer matchedTransactions,
        Integer unmatchedTransaction,
        Integer paidInvoices,
        Integer partiallyPaidInvoice,
        Integer outstandingInvoices,
        Integer disputedInvoices,
        ReconciliationReportCreateResponseDTO reportSummary
) {}
