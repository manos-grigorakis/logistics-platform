package com.manosgrigorakis.logisticsplatform.dto.quote;

import com.manosgrigorakis.logisticsplatform.enums.QuoteStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class QuoteCreatedResponseDTO {
    private Long id;
    private String number;
    private LocalDate issueDate;
    private LocalDate expirationDate;
    private BigDecimal grossPrice;
    private QuoteStatus quoteStatus;
    private String pdfUrl;
    private Long customerId;
    private String customerFullName;
}
