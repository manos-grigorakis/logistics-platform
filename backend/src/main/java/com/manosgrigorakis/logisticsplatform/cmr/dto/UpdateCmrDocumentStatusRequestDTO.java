package com.manosgrigorakis.logisticsplatform.cmr.dto;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCmrDocumentStatusRequestDTO {
    @NotNull(message = "CMR status is required")
    private CmrStatus status;
}
