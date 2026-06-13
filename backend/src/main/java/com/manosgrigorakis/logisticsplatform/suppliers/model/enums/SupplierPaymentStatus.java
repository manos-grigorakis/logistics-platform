package com.manosgrigorakis.logisticsplatform.suppliers.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum SupplierPaymentStatus {
    @JsonProperty("pending")
    PENDING(false),

    @JsonProperty("paid")
    PAID(true),

    @JsonProperty("partially_paid")
    PARTIALLY_PAID(false),

    @JsonProperty("canceled")
    CANCELED(true),;

    private final boolean isFinal;

    SupplierPaymentStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }
}
