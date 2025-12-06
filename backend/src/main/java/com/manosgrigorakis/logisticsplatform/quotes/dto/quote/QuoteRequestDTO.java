package com.manosgrigorakis.logisticsplatform.quotes.dto.quote;

import com.manosgrigorakis.logisticsplatform.quotes.dto.quoteItem.QuoteItemRequestDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuoteRequestDTO {
    @NotNull(message = "Validity days are required")
    @Positive(message = "Validity days must be a positive number")
    private Integer validityDays;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @Nullable
    private String notes;

    @Nullable
    private String specialTerms;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotNull(message = "Quote items are required")
    @NotEmpty(message = "At least one quote item is required")
    private List<QuoteItemRequestDTO> quoteItems;
}
