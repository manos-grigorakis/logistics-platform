package com.manosgrigorakis.logisticsplatform.payments.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum InvoiceStatus {
    @JsonProperty("paid")
    PAID,

    @JsonProperty("disputed")
    DISPUTED,

    @JsonProperty("outstanding")
    OUTSTANDING,

    @JsonProperty("partially_paid")
    PARTIALLY_PAID
}
