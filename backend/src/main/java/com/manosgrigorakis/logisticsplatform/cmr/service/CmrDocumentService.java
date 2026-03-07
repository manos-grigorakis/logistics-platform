package com.manosgrigorakis.logisticsplatform.cmr.service;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentFilterRequest;
import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.dto.UpdateCmrDocumentStatusRequestDTO;
import com.manosgrigorakis.logisticsplatform.cmr.dto.UploadCmrDocumentRequestDTO;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface CmrDocumentService {
    Page<CmrDocumentResponseDTO> getAllCmrDocuments(
            CmrDocumentFilterRequest filterRequest,
            PageFilterRequest page,
            SortFilterRequest sort
    );

    CmrDocumentResponseDTO getCmrDocumentById(Long id);

    void createCmrDocument(Quote quote, Shipment shipment);

    void updateCmrDocumentStatus(Long id, UpdateCmrDocumentStatusRequestDTO dto);

    void uploadSignedCmrDocument(Long id, MultipartFile file, UploadCmrDocumentRequestDTO dto);
}
