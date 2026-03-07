package com.manosgrigorakis.logisticsplatform.cmr.controller;

import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentFilterRequest;
import com.manosgrigorakis.logisticsplatform.cmr.dto.CmrDocumentResponseDTO;
import com.manosgrigorakis.logisticsplatform.cmr.dto.UpdateCmrDocumentStatusRequestDTO;
import com.manosgrigorakis.logisticsplatform.cmr.dto.UploadCmrDocumentRequestDTO;
import com.manosgrigorakis.logisticsplatform.cmr.service.CmrDocumentService;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cmr-documents")
@Tag(name = "CMR Documents")
public class CmrDocumentRestController {
    private final CmrDocumentService cmrDocumentService;

    public CmrDocumentRestController(CmrDocumentService cmrDocumentService) {
        this.cmrDocumentService = cmrDocumentService;
    }

    @Operation(
            summary = "Get all CMR documents",
            description = "Get the CMR documents collection with pagination, filtering and sorting"
    )
    @ApiResponse(responseCode = "200", description = "Founded CMR documents")
    @GetMapping
    public Page<CmrDocumentResponseDTO> getAllCmrDocuments(
            @ParameterObject @ModelAttribute @Valid CmrDocumentFilterRequest filterRequest,
            @ParameterObject @ModelAttribute @Valid PageFilterRequest page,
            @ParameterObject @ModelAttribute SortFilterRequest sort
            )
    {
        return this.cmrDocumentService.getAllCmrDocuments(filterRequest, page, sort);
    }

    @Operation(summary = "Get CMR Document by Id", description = "Find CMR document by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded CMR document"),
            @ApiResponse(responseCode = "404", description = "CMR document id doesn't exist"),
    })
    @GetMapping("/{id}")
    public CmrDocumentResponseDTO getCmrDocumentById(@PathVariable Long id) {
        return this.cmrDocumentService.getCmrDocumentById(id);
    }

    @Operation(summary = "Updates CMR document Status", description = "Updates the status of the CMR document by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "CMR document status updated successfully"),
            @ApiResponse(responseCode = "404", description = "CMR document id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "CMR document status cannot be updated due to business rules"),
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateCmrDocumentStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCmrDocumentStatusRequestDTO dto
            )
    {
        this.cmrDocumentService.updateCmrDocumentStatus(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Uploads Signed CMR Document",
            description = "Upload of the signed CMR document by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "CMR signed document uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "CMR document id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "CMR document is already signed"),
    })
    @PostMapping(value = "/{id}/signed-copy", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadSignedCmrDocument(
            @PathVariable Long id,
            @ModelAttribute @Valid UploadCmrDocumentRequestDTO dto
    ) {
        this.cmrDocumentService.uploadSignedCmrDocument(id, dto);

        return ResponseEntity.noContent().build();
    }
}
