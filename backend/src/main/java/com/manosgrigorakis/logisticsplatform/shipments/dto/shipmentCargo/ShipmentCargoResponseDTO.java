package com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo;

import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentCargoUnit;

import java.math.BigDecimal;

public record ShipmentCargoResponseDTO(
        Long id,
        String description,
        ShipmentCargoUnit unit,
        Integer quantity,
        BigDecimal weightKg,
        BigDecimal volumeM3
) {}
