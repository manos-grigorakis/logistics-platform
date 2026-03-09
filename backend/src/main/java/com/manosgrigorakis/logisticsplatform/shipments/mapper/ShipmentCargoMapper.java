package com.manosgrigorakis.logisticsplatform.shipments.mapper;

import com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo.ShipmentCargoRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo.ShipmentCargoResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.model.ShipmentCargo;

public class ShipmentCargoMapper {
    // Request => Entity
    public static ShipmentCargo toEntity(ShipmentCargoRequestDTO dto) {
        return ShipmentCargo.builder()
                .description(dto.getDescription())
                .unit(dto.getUnit())
                .quantity(dto.getQuantity())
                .weightKg(dto.getWeightKg())
                .volumeM3(dto.getVolumeM3())
                .build();
    }

    // Entity => Response
    public static ShipmentCargoResponseDTO toResponse(ShipmentCargo shipmentCargo) {
        return new ShipmentCargoResponseDTO(
                shipmentCargo.getId(),
                shipmentCargo.getDescription(),
                shipmentCargo.getUnit(),
                shipmentCargo.getQuantity(),
                shipmentCargo.getWeightKg(),
                shipmentCargo.getVolumeM3()
        );
    }
}
