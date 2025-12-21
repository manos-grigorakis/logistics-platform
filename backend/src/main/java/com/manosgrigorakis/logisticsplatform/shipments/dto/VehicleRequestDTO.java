package com.manosgrigorakis.logisticsplatform.shipments.dto;

import com.manosgrigorakis.logisticsplatform.shipments.enums.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequestDTO {
    @NotNull(message = "Brand is required")
    private String brand;

    @NotNull(message = "Plate is required")
    @Size(min = 8, max = 8)
    private String plate;

    @NotNull(message = "Type is required")
    private VehicleType type;
}
