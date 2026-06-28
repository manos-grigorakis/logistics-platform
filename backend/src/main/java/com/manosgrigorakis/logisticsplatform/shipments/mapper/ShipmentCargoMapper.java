package com.manosgrigorakis.logisticsplatform.shipments.mapper;

import com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo.ShipmentCargoRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo.ShipmentCargoResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.model.ShipmentCargo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShipmentCargoMapper {
    ShipmentCargo toEntity(ShipmentCargoRequestDTO dto);

    ShipmentCargoResponseDTO toResponse(ShipmentCargo entity);
}
