package com.manosgrigorakis.logisticsplatform.quotes.dto.quote;

import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuoteStatusRequestDTO {
    @NotNull(message = "Quote status is required")
    private QuoteStatus quoteStatus;
}
