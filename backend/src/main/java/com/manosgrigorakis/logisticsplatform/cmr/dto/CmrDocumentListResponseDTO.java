package com.manosgrigorakis.logisticsplatform.cmr.dto;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CmrDocumentListResponseDTO {
    private Long id;
    private Long shipmentId;
    private String number;
    private CmrStatus status;
    private LocalDateTime signedAt;
    private String signedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
