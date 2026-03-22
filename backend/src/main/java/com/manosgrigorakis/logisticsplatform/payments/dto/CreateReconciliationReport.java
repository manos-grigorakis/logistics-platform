package com.manosgrigorakis.logisticsplatform.payments.dto;

import com.manosgrigorakis.logisticsplatform.customers.model.Customer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

public record CreateReconciliationReport(
        ByteArrayOutputStream file,
        LocalDate fromDate,
        LocalDate toDate,
        Integer matchedInvoices,
        Integer unmatchedInvoices,
        Customer customer
) {}
