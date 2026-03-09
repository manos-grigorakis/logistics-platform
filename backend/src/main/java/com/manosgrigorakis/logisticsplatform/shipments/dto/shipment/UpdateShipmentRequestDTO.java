package com.manosgrigorakis.logisticsplatform.shipments.dto.shipment;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateShipmentRequestDTO {
    @Nullable
    private Long driverId;

    @Nullable
    private Long truckId;

    @Nullable
    private Long trailerId;

    @NotNull(message = "Pickup datetime is required")
    private LocalDateTime pickup;

    @Nullable
    private String notes;
}
