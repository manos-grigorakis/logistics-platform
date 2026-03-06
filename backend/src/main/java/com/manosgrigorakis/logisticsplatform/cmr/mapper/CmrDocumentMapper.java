package com.manosgrigorakis.logisticsplatform.cmr.mapper;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.model.CmrDocument;

public class CmrDocumentMapper {
    // CmrDocument -> Response
    public static CmrDocumentResponseDTO toResponse(CmrDocument cmrDocument) {
        CmrDocumentResponseDTO response = new CmrDocumentResponseDTO();
        response.setId(cmrDocument.getId());
        response.setNumber(cmrDocument.getNumber());
        response.setStatus(cmrDocument.getStatus());
        response.setFileUrl(cmrDocument.getFileUrl());
        response.setSignedAt(cmrDocument.getSignedAt());
        response.setSignedBy(cmrDocument.getSignedBy());
        response.setCreatedAt(cmrDocument.getCreatedAt());
        response.setUpdatedAt(cmrDocument.getUpdatedAt());

        if(cmrDocument.getShipment() != null) {
            response.setShipmentId(cmrDocument.getShipment().getId());
        }

        return response;
    }
}
