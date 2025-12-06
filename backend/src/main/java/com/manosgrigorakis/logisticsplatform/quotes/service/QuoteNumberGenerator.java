package com.manosgrigorakis.logisticsplatform.quotes.service;

import org.springframework.stereotype.Component;

@Component
public class QuoteNumberGenerator {
    /**
     * Generates the next sequential number based on the last number
     * Quote number format: Q-YYYY-NNNN (e.g. Q-2025-0001)
     * @param lastNumber The last issued quote number (e.g. Q-2025-0004)
     * @return The next quote number in sequence (e.g. Q-2025-0005)
     */
    public String generateNextQuoteNumber(String lastNumber) {
        String[] parts = lastNumber.split("-");
        int year = Integer.parseInt(parts[1]);
        int sequence = Integer.parseInt(parts[2]);
        int nextSequence = sequence + 1;

        return String.format("Q-%d-%04d", year, nextSequence);
    }
}
