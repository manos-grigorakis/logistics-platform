package com.manosgrigorakis.logisticsplatform.enums;

public enum QuoteStatus {
    DRAFT,
    SENT,
    ACCEPTED,
    EXPIRED;

    /**
     * Returns {@code true} if the quote status is SENT, ACCEPTED, EXPIRED
     * otherwise returns {@code false}
     */
    public boolean isFinal() {
        return this == SENT || this == ACCEPTED || this == EXPIRED;
    }
}
