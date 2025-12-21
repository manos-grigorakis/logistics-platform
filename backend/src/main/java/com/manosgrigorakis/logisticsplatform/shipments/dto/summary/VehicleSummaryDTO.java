package com.manosgrigorakis.logisticsplatform.shipments.dto.summary;

import com.manosgrigorakis.logisticsplatform.shipments.enums.VehicleType;

public record VehicleSummaryDTO(
        Long id,
        String plate,
        VehicleType type
)
{}
