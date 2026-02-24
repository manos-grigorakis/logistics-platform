package com.manosgrigorakis.logisticsplatform.quotes.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuoteStatus {
    @JsonProperty("draft")
    DRAFT,

    @JsonProperty("sent")
    SENT,

    @JsonProperty("accepted")
    ACCEPTED,

    @JsonProperty("rejected")
    REJECTED,

    @JsonProperty("cancelled")
    CANCELLED,

    @JsonProperty("expired")
    EXPIRED,

    @JsonProperty("converted")
    CONVERTED;

    /**
     * Returns {@code true} if the quote status is ACCEPTED, REJECTED, CANCELLED, EXPIRED
     * otherwise returns {@code false}
     */
    public boolean isFinal() {
        return this == ACCEPTED || this == REJECTED || this == CANCELLED || this == EXPIRED || this == CONVERTED;
    }

    /**
     * Returns {@code true} if quote status is EXPIRED
     * otherwise returns {@code false}
     */
    public boolean isExpired() {
        return this == EXPIRED;
    }
}
