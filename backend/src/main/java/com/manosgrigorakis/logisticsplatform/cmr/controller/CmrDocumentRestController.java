package com.manosgrigorakis.logisticsplatform.cmr.controller;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentFilterRequest;
import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.dto.UpdateCmrDocumentStatusRequestDTO;
import com.manosgrigorakis.logisticsplatform.cmr.dto.UploadCmrDocumentRequestDTO;
import com.manosgrigorakis.logisticsplatform.cmr.service.CmrDocumentService;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public void updateCmrDocumentStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCmrDocumentStatusRequestDTO dto
            )
    {
        this.cmrDocumentService.updateCmrDocumentStatus(id, dto);
    }

    @PostMapping("/{id}/signed-copy")
    public ResponseEntity<Void> uploadSignedCmrDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @ModelAttribute @Valid UploadCmrDocumentRequestDTO dto
    ) {
        this.cmrDocumentService.uploadSignedCmrDocument(id, file, dto);

        return ResponseEntity.noContent().build();
    }
}
