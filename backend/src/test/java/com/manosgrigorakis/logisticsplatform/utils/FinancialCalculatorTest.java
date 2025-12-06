package com.manosgrigorakis.logisticsplatform.utils;

import com.manosgrigorakis.logisticsplatform.common.utils.FinancialCalculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class FinancialCalculatorTest {
    @Test
    public void calculateVatAmount_shouldReturnCorrectAmount() {
        // Arrange
        BigDecimal netAmount = new BigDecimal("100");

        // Act
        BigDecimal vatAmount = FinancialCalculator.calculateVatAmount(netAmount, 24);

        // Assert
        assertEquals(0, new BigDecimal("24.00").compareTo(vatAmount));
    }

    @Test
    public void getVatRate_shouldReturnCorrectRate() {
        // Arrange
        int vat = 24;

        // Act
        BigDecimal vatRate = FinancialCalculator.getVatRate(vat);

        // Assert
        assertEquals(0, new BigDecimal("0.22").compareTo(vatRate));
    }

    @Test
    public void calculateGrossTotal_shouldReturnCorrectGrossTotal() {
        // Arrange
        BigDecimal netAmount = new BigDecimal("100");
        BigDecimal vatAmount = new BigDecimal("24");

        // Act
        BigDecimal grossTotal = FinancialCalculator.calculateGrossTotal(netAmount, vatAmount);

        // Assert
        assertEquals(0, new BigDecimal("124.00").compareTo(grossTotal));
    }
}
