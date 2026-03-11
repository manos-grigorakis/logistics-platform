package com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo;

import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentCargoUnit;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShipmentCargoRequestDTO {
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Unit is required")
    private ShipmentCargoUnit unit;

    @Min(1)
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Positive
    @NotNull(message = "Weight KG is required")
    private BigDecimal weightKg;

    @Nullable
    private BigDecimal volumeM3;
}
