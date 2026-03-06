package com.manosgrigorakis.logisticsplatform.cmr.dto;


import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CmrDocumentResponseDTO {
    private Long id;
    private Long shipmentId;
    private String number;
    private CmrStatus status;
    private String fileUrl;
    private LocalDateTime signedAt;
    private String signedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
