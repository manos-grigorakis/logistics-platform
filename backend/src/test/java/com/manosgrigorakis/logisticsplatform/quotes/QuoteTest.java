package com.manosgrigorakis.logisticsplatform.quotes;

import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteTest {
    @Test
    void calculatesDates_shouldCalculateCorrectExpiration() {
        // Arrange
        LocalDate today = LocalDate.now();
        int validityDays = 20;

        Quote quote = new Quote();
        quote.setValidityDays(validityDays);

        // Act
        quote.onPersist();

        // Assert
        assertEquals(today, quote.getIssueDate());
        assertEquals(today.plusDays(validityDays), quote.getExpirationDate());
        assertNotNull(quote.getCreatedAt());
    }
}
