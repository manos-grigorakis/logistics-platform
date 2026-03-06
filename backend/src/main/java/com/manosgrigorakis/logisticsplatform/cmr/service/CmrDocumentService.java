package com.manosgrigorakis.logisticsplatform.cmr.service;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentFilterRequest;
import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import org.springframework.data.domain.Page;

public interface CmrDocumentService {
    Page<CmrDocumentResponseDTO> getAllCmrDocuments(
            CmrDocumentFilterRequest filterRequest,
            PageFilterRequest page,
            SortFilterRequest sort
    );

    CmrDocumentResponseDTO getCmrDocumentById(Long id);

    void createCmrDocument(Shipment shipment);

    CmrDocumentResponseDTO updateCmrDocumentStatus(Long id);

    CmrDocumentResponseDTO uploadSignedCmrDocument(Long id);
}
