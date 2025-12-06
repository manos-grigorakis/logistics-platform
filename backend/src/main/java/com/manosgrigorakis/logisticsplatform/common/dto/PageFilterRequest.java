package com.manosgrigorakis.logisticsplatform.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageFilterRequest {
    @Min(0)
    @Schema(title = "Page Number", description = "Page number", example = "0", defaultValue = "0")
    private int page = 0;

    @Min(1)
    @Max(50)
    @Schema(title = "Page Size",description = "Page size", example = "10", defaultValue = "10")
    private int size = 10;
}
