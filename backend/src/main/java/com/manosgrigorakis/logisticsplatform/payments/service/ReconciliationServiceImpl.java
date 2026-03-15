package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReconciliationServiceImpl implements ReconciliationService {
    private final InvoiceService invoiceService;


    public ReconciliationServiceImpl(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    @Transactional
    public void reconciliationProcess(ReconciliationRequestDTO dto) {
        List<Invoice> invoices =
                this.invoiceService.prepareInvoicesForReconciliation(dto.getCustomerId(), dto.getInvoiceFile());
    }
}
