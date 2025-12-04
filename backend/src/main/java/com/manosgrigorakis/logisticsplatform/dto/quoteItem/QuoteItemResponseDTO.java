package com.manosgrigorakis.logisticsplatform.dto.quoteItem;

import com.manosgrigorakis.logisticsplatform.enums.QuoteItemUnit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class QuoteItemResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Integer quantity;
    private QuoteItemUnit unit;
    private BigDecimal price;
}
