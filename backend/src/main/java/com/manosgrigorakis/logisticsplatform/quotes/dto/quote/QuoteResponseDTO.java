package com.manosgrigorakis.logisticsplatform.quotes.dto.quote;

import com.manosgrigorakis.logisticsplatform.quotes.dto.CustomerSummaryDTO;
import com.manosgrigorakis.logisticsplatform.quotes.dto.quoteItem.QuoteItemResponseDTO;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class QuoteResponseDTO {
    private Long id;
    private String number;
    private String pdfUrl;
    private LocalDate issueDate;
    private Integer validityDays ;
    private LocalDate expirationDate;
    private String origin;
    private String destination;
    private Integer taxRatePercentage;
    private BigDecimal netPrice;
    private BigDecimal vatAmount;
    private BigDecimal grossPrice;
    private String notes;
    private String specialTerms;
    private QuoteStatus quoteStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private CustomerSummaryDTO customer;
    private List<QuoteItemResponseDTO> quoteItems;
}
