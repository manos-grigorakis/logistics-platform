package com.manosgrigorakis.logisticsplatform.common.generators;

import org.springframework.stereotype.Component;

@Component
public class DocumentNumberGenerator {
    /**
     * Generates the next sequential number based on the last number.
     * Document number format: X-YYYY-NNNN (e.g. Q-2025-0001, S-2025-0025)
     * @param code The prefix starting code (e.g Q, S)
     * @param lastNumber The last issued document number (e.g. Q-2025-0004)
     * @return The next document number in sequence (e.g. Q-2025-0005)
     */
    public String generateNextSequentialNumber(String code, String lastNumber) {
        String[] parts = lastNumber.split("-");
        int year = Integer.parseInt(parts[1]);
        int sequence = Integer.parseInt(parts[2]);
        int nextSequence = sequence + 1;

        return String.format("%s-%d-%04d", code, year, nextSequence);
    }
}
