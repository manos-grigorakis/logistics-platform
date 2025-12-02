package com.manosgrigorakis.logisticsplatform.dto.quote;

import com.manosgrigorakis.logisticsplatform.dto.quoteItem.QuoteItemResponseDTO;
import com.manosgrigorakis.logisticsplatform.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.model.Customer;
import com.manosgrigorakis.logisticsplatform.model.QuoteItem;
import com.manosgrigorakis.logisticsplatform.model.User;
import jakarta.persistence.*;
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
    private LocalDate issueDate;
    private Integer validityDays ;
    private LocalDate expirationDate;
    private String origin;
    private String destination;
    private BigDecimal price;
    private String notes;
    private String specialTerms;
    private QuoteStatus quoteStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private Long customerId;
    private List<QuoteItemResponseDTO> quoteItems;
}
