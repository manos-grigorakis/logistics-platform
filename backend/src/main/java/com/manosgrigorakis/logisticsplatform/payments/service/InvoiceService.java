package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceResponseDTO;

public interface InvoiceService {
    BulkInvoiceResponseDTO bulkInvoicesImport(BulkInvoiceRequestDTO dto);
}
