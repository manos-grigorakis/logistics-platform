package com.manosgrigorakis.logisticsplatform.shipments.mapper;

import com.manosgrigorakis.logisticsplatform.shipments.dto.VehicleResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;

public class VehicleMapper {
    public static VehicleResponseDTO toResponse(Vehicle vehicle) {
        return new VehicleResponseDTO(
                vehicle.getId(),
                vehicle.getBrand(),
                vehicle.getPlate(),
                vehicle.getType(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}
