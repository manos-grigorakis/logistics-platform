package com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle;

import com.manosgrigorakis.logisticsplatform.shipments.enums.VehicleType;

import java.time.LocalDateTime;

public record VehicleResponseDTO(
        Long id,
        String brand,
        String plate,
        VehicleType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
)
{ }
