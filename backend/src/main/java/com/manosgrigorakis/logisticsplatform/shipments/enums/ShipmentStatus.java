package com.manosgrigorakis.logisticsplatform.shipments.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShipmentStatus {
    @JsonProperty("pending")
    PENDING,

    @JsonProperty("dispatched")
    DISPATCHED,

    @JsonProperty("delivered")
    DELIVERED
}
