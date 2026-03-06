package com.manosgrigorakis.logisticsplatform.cmr.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CmrStatus {
    @JsonProperty("generated")
    GENERATED,

    @JsonProperty("signed")
    SIGNED,

    @JsonProperty("cancelled")
    CANCELLED;

    /**
     * Return {@code true} is CMR status is CANCELLED, SIGNED
     * otherwise returns {@code false}
     */
    public Boolean isFinal() {
        return this == CANCELLED || this == SIGNED;
    }
}
