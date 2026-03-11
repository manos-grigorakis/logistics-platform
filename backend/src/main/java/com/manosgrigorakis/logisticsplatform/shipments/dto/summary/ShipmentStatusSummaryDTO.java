package com.manosgrigorakis.logisticsplatform.shipments.dto.summary;

public record ShipmentStatusSummaryDTO(
        String label,
        boolean isEditable,
        boolean isFinalized
) {
}
