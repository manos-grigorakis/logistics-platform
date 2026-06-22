package com.manosgrigorakis.logisticsplatform.infrastructure.document.dto;

import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import com.manosgrigorakis.logisticsplatform.companyprofile.model.CompanyProfile;
import com.manosgrigorakis.logisticsplatform.infrastructure.document.PdfRequest;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;

public record CmrDocumentPdfRequestDTO(Quote quote, Shipment shipment, CmrDocument cmrDocument,
                                       CompanyProfile companyProfile) implements PdfRequest {
    @Override
    public String getDocumentType() {
        return cmrDocument.getClass().getSimpleName();
    }

    @Override
    public String getDocumentIdentifier() {
        return cmrDocument().getNumber();
    }
}
