package com.manosgrigorakis.logisticsplatform.shipments.dto.shipment;

public record ShipmentStatusResponse(
    String label,
    boolean isEditable,
    boolean isFinalized
)
{}
