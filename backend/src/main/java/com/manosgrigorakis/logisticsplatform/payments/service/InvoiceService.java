package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceResponseDTO;
import com.manosgrigorakis.logisticsplatform.payments.dto.PrepareReconciliationResult;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InvoiceService {
    BulkInvoiceResponseDTO bulkInvoicesImport(BulkInvoiceRequestDTO dto);

    PrepareReconciliationResult prepareInvoicesForReconciliation(Long customerId, MultipartFile file);
}
