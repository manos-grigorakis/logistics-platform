package com.manosgrigorakis.logisticsplatform.cmr.controller;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentFilterRequest;
import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.service.CmrDocumentService;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cmr-documents")
public class CmrDocumentRestController {
    private final CmrDocumentService cmrDocumentService;

    public CmrDocumentRestController(CmrDocumentService cmrDocumentService) {
        this.cmrDocumentService = cmrDocumentService;
    }

    @GetMapping
    public Page<CmrDocumentResponseDTO> getAllCmrDocuments(
            @ParameterObject @ModelAttribute @Valid CmrDocumentFilterRequest filterRequest,
            @ParameterObject @ModelAttribute @Valid PageFilterRequest page,
            @ParameterObject @ModelAttribute SortFilterRequest sort
            )
    {
        return this.cmrDocumentService.getAllCmrDocuments(filterRequest, page, sort);
    }

    @GetMapping("/{id}")
    public CmrDocumentResponseDTO getCmrDocumentById(@PathVariable Long id) {
        return this.cmrDocumentService.getCmrDocumentById(id);
    }

    @PatchMapping("/{id}/status")
    public CmrDocumentResponseDTO updateCmrDocumentStatus(@PathVariable Long id) {
        return null;
    }

    @PatchMapping("/{id}/signed-copy")
    public CmrDocumentResponseDTO uploadSignedCmrDocument(@PathVariable Long id) {
        return null;
    }
}
