package com.manosgrigorakis.logisticsplatform.cmr.dto;

import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;

public record RegenerateCmrDocumentPdf(CmrDocument cmrDocument, Shipment shipment, Quote quote) {
}
