package com.manosgrigorakis.logisticsplatform.payments.dto;

import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;

import java.util.List;

public record PrepareReconciliationResult(
        Customer customer,
        List<Invoice> invoices
) {}
