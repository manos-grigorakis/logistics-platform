package com.manosgrigorakis.logisticsplatform.cmr.dto;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCmrDocumentStatusRequestDTO {
    @Schema(description = "CMR Status", example = "SIGNED")
    @NotNull(message = "CMR status is required")
    private CmrStatus status;
}
