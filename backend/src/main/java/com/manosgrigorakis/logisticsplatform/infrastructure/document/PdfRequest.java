package com.manosgrigorakis.logisticsplatform.infrastructure.document;

public interface PdfRequest {
    /**
     * Return the logical document type (e.g. Quote, CMR)
     * @return The document type used for logging and identification
     */
    String getDocumentType();

    /**
     * The unique identifier of the document (e.g. CMR-2025-0001)
     * @return The unique document identifier of the document
     */
    String getDocumentIdentifier();
}
