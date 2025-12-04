package com.manosgrigorakis.logisticsplatform.dto.quote;

import com.manosgrigorakis.logisticsplatform.enums.QuoteStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class QuoteListResponseDTO {
    private Long id;
    private String number;
    private QuoteStatus status;
    private BigDecimal grossPrice;
    private LocalDate issueDate;
    private String companyName;
}
