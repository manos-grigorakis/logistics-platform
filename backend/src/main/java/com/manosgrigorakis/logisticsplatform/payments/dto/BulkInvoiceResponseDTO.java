package com.manosgrigorakis.logisticsplatform.payments.dto;

public record BulkInvoiceResponseDTO(
   Integer totalRows,
   Integer imported,
   Integer skipped
) {}
