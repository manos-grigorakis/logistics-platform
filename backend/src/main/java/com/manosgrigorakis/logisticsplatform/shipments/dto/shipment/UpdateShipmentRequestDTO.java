package com.manosgrigorakis.logisticsplatform.shipments.dto.shipment;

import com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo.ShipmentCargoRequestDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @Valid
    private List<ShipmentCargoRequestDTO> cargoItems;
}
