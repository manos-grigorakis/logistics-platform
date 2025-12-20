package com.manosgrigorakis.logisticsplatform.customers.dto;

import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotesPerCustomerFilterRequest {
    @Schema(title = "Quote Number", description = "Quote number", example = "Q-2025-0004")
    private String number;

    @Schema(title = "Quote Status", description = "Quote status")
    private QuoteStatus quoteStatus;
}
