package com.manosgrigorakis.logisticsplatform.payments.mapper;

import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.ExcelInvoiceImportDTO;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;

public class InvoiceMapper {
    // Excel Invoice Import -> Entity
    public static Invoice toEntity(ExcelInvoiceImportDTO dto) {
        return Invoice.builder()
                .externalInvoiceNumber(dto.number())
                .totalAmount(dto.amount())
                .invoiceDate(dto.issueDate())
                .build();
    }
}
