package com.manosgrigorakis.logisticsplatform.quotes;

import com.manosgrigorakis.logisticsplatform.enums.QuoteItemUnit;
import com.manosgrigorakis.logisticsplatform.model.Quote;
import com.manosgrigorakis.logisticsplatform.model.QuoteItem;
import com.manosgrigorakis.logisticsplatform.service.impl.QuoteCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteCalculatorTest {
    @Test
    void calculateNetTotal_shouldReturnNetTotal() {
        // Arrange
        Quote quote =  new Quote();
        QuoteItem quoteItem1 =  QuoteItem.builder()
                .name("Item 1")
                .description("Test item")
                .quantity(2)
                .unit(QuoteItemUnit.PIECE)
                .price(BigDecimal.valueOf(50))
                .build();

        QuoteItem quoteItem2 =  QuoteItem.builder()
                .name("Item 2")
                .description("Test item")
                .quantity(20)
                .unit(QuoteItemUnit.PALLET)
                .price(BigDecimal.valueOf(30))
                .build();

        quote.addQuoteItem(quoteItem1);
        quote.addQuoteItem(quoteItem2);

        // Act
        BigDecimal netTotal = new QuoteCalculator().calculateNetTotal(quote);

        // Assert
        assertEquals(0, new BigDecimal("700.00").compareTo(netTotal));
        assertEquals(2, netTotal.scale());
    }
}
