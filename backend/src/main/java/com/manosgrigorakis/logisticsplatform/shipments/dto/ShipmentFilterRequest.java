package com.manosgrigorakis.logisticsplatform.shipments.dto;

import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentFilterRequest {
    @Schema(title = "Shipment Number", description = "Shipment number", example = "TO-2025-0004")
    private String number;

    @Schema(title = "Shipment Status", example = "DISPATCHED")
    private ShipmentStatus status;

    @Schema(title = "Pickup date from", example = "2025-01-15T00:00:00")
    private LocalDateTime pickupFrom;

    @Schema(title = "Pickup date to", example = "2025-01-15T23:59:59")
    private LocalDateTime pickupTo;
}
