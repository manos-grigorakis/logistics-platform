package com.manosgrigorakis.logisticsplatform.cmr.dto;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CmrDocumentFilterRequest {
    @Schema(title = "CMR Document Number", description = "CMR document number", example = "CMR-2025-0004")
    private String number;

    @Schema(title = "CMR Status", description = "CMR status")
    private CmrStatus status;
}
