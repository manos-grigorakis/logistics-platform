package com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo;

import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentCargoUnit;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShipmentCargoRequestDTO {
    @Null(message = "Description is required")
    private String description;

    @Null(message = "Unit is required")
    private ShipmentCargoUnit unit;

    @Min(1)
    @Null(message = "Quantity is required")
    private Integer quantity;

    @Null(message = "Weight KG is required")
    private BigDecimal weightKg;

    @Nullable
    private BigDecimal volumeM3;
}
