package com.manosgrigorakis.logisticsplatform.infrastructure.document.dto;

import com.manosgrigorakis.logisticsplatform.infrastructure.document.PdfRequest;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;

public record QuotePdfRequestDTO (Quote quote) implements PdfRequest {
    @Override
    public String getDocumentType() {
        return this.quote.getClass().getSimpleName();
    }

    @Override
    public String getDocumentIdentifier() {
        return quote.getNumber();
    }
}
