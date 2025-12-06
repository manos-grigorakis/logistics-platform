package com.manosgrigorakis.logisticsplatform.quotes.dto.quote;

import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteFilterRequest {
    @Schema(title = "Quote Number", description = "Quote number", example = "Q-2025-0004")
    private String number;

    @Schema(title = "Company Name", description = "Company name", example = "ACME Corp")
    private String companyName;

    @Schema(title = "Quote Status", description = "Quote status")
    private QuoteStatus quoteStatus;
}
