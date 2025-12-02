package com.manosgrigorakis.logisticsplatform.dto.quoteItem;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class QuoteItemRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @Nullable
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin("0.00")
    @Digits(integer = 19, fraction = 4)
    private BigDecimal price;
}
