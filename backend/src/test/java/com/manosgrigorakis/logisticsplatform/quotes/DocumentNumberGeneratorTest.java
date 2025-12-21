package com.manosgrigorakis.logisticsplatform.quotes;

import com.manosgrigorakis.logisticsplatform.common.generators.DocumentNumberGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class DocumentNumberGeneratorTest {
    @Test
    void quoteNumberGeneratorTests_shouldReturnCorrectNumber() {
        // Arrange
        int currentYear = LocalDate.now().getYear();
        String lastNumber = "Q-" + currentYear + "-0005";

        // Act
        DocumentNumberGenerator documentNumberGenerator = new DocumentNumberGenerator();
        String result = documentNumberGenerator.generateNextSequentialNumber("Q", lastNumber);

        // Assert
        assertEquals("Q-" + currentYear + "-0006", result);
    }
}
