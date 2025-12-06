package com.manosgrigorakis.logisticsplatform.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class FinancialCalculator {
    /**
     * Converts VAT percentage to VAT rate
     * @return The factor of vatPercent (e.g. 24 -> 0.24)
     */
    public static BigDecimal getVatRate(int vatPercent) {
        return BigDecimal.valueOf(vatPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the VAT amount based on net total,
     * using the configured tax rate
     * @param netTotal Amount before tax
     * @return Calculated VAT amount
     */
    public static BigDecimal calculateVatAmount(BigDecimal netTotal, int vatPercent) {
        return netTotal.multiply(getVatRate(vatPercent)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the gross total by adding net amount and VAT
     * @param netTotal Amount before tax
     * @param vatAmount Calculated tax amount
     * @return gross total, rounded to 2 decimal
     */
    public static BigDecimal calculateGrossTotal(BigDecimal netTotal, BigDecimal vatAmount) {
        return netTotal.add(vatAmount)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
