package com.manosgrigorakis.logisticsplatform.payments.controller;

import com.manosgrigorakis.logisticsplatform.payments.dto.BulkInvoiceRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceRestController {
    private final InvoiceService invoiceService;

    public InvoiceRestController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(value = "/bulk-import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> bulkInvoicesImport(@ModelAttribute @Valid BulkInvoiceRequestDTO dto) {
        this.invoiceService.bulkInvoicesImport(dto);
        return ResponseEntity.noContent().build();
    }
}
