package com.manosgrigorakis.logisticsplatform.payments.mapper;

import com.manosgrigorakis.logisticsplatform.infrastructure.document.dto.ExcelInvoiceImportDTO;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    @Mapping(target = "externalInvoiceNumber", source = "number")
    @Mapping(target = "totalAmount", source = "amount")
    @Mapping(target = "invoiceDate", source = "issueDate")
    Invoice toEntity(ExcelInvoiceImportDTO dto);
}
