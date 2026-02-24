package com.manosgrigorakis.logisticsplatform.shipments.dto;

public record ShipmentStatusResponse(
    String label,
    boolean isEditable,
    boolean isFinalized
)
{}
