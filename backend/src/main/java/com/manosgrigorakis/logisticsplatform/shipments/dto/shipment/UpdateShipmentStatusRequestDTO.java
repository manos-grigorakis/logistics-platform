package com.manosgrigorakis.logisticsplatform.shipments.dto.shipment;

import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public record UpdateShipmentStatusRequestDTO(
   @NotNull(message = "Shipment status is required")
   ShipmentStatus status
) {}
