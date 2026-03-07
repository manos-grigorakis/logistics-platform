package com.manosgrigorakis.logisticsplatform.cmr.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadCmrDocumentRequestDTO {
    @NotNull(message = "Signed by is required")
    private String signedBy;
}
