package com.manosgrigorakis.logisticsplatform.payments.dto;

import com.manosgrigorakis.logisticsplatform.payments.model.BankTransaction;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import com.manosgrigorakis.logisticsplatform.payments.model.InvoicePayments;

import java.util.List;

public record MultipleInvoicesMatchingResults(
        List<Invoice> matchedInvoices,
        List<BankTransaction> matchedTransactions,
        List<InvoicePayments> invoicePayments
) {}
