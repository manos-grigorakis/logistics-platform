package com.manosgrigorakis.logisticsplatform.shipments.dto.summary;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;

import java.time.LocalDateTime;

public record CmrDocumentSummary(
        Long id,
        String number,
        CmrStatus status,
        String fileUrl,
        String signedBy,
        LocalDateTime signedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
