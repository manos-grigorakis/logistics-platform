package com.manosgrigorakis.logisticsplatform.filters;

import com.manosgrigorakis.logisticsplatform.enums.QuoteStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteFilterRequest {
    private String number;
    private String companyName;
    private QuoteStatus quoteStatus;
}
