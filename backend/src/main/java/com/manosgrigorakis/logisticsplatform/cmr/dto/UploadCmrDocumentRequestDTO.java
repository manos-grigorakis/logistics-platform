package com.manosgrigorakis.logisticsplatform.cmr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadCmrDocumentRequestDTO {
    @Schema(name = "Indicates that the sender has signed")
    @NotNull(message = "Sender signed is required")
    private Boolean senderSigned;

    @Schema(name = "Indicates that the carrier has signed")
    @NotNull(message = "Carrier signed is required")
    private Boolean carrierSigned;

    @Schema(name = "Indicates that the consignee has signed")
    @NotNull(message = "Consignee signed is required")
    private Boolean consigneeSigned;

    @Schema(type = "string", format = "binary", description = "Signed CMR PDF file")
    @NotNull(message = "Signed PDF file is required")
    private MultipartFile file;
}
