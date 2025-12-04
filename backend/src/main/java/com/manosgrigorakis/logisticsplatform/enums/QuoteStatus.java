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
        return this == ACCEPTED || this == EXPIRED;
    }

    /**
     * Returns {@code true} if quote status is EXPIRED
     * otherwise returns {@code false}
     */
    public boolean isExpired() {
        return this == EXPIRED;
    }
}
