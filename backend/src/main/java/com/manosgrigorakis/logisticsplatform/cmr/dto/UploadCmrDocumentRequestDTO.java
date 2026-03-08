package com.manosgrigorakis.logisticsplatform.cmr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadCmrDocumentRequestDTO {
    @Schema(type = "string", format = "binary", description = "Signed CMR PDF file")
    @NotNull(message = "Signed PDF file is required")
    private MultipartFile file;

    @Schema(name = "Name of the person who signed")
    @NotNull(message = "Signed by is required")
    private String signedBy;
}
