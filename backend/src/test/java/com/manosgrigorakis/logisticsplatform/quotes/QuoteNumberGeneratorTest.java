package com.manosgrigorakis.logisticsplatform.quotes;

import com.manosgrigorakis.logisticsplatform.quotes.service.QuoteNumberGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class QuoteNumberGeneratorTest {
    @Test
    void quoteNumberGeneratorTests_shouldReturnCorrectNumber() {
        // Arrange
        int currentYear = LocalDate.now().getYear();
        String lastNumber = "Q-" + currentYear + "-0005";

        // Act
        QuoteNumberGenerator quoteNumberGenerator = new QuoteNumberGenerator();
        String result = quoteNumberGenerator.generateNextQuoteNumber(lastNumber);

        // Assert
        assertEquals("Q-" + currentYear + "-0006", result);
    }
}
