package com.manosgrigorakis.logisticsplatform.shipments.mapper;

import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    Vehicle toEntity(VehicleRequestDTO dto);

    void toUpdate(@MappingTarget Vehicle vehicle, VehicleRequestDTO dto);

    VehicleResponseDTO toResponse(Vehicle vehicle);
}
