package com.manosgrigorakis.logisticsplatform.shipments.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VehicleType {
    @JsonProperty("truck")
    TRUCK,

    @JsonProperty("trailer")
    TRAILER
}
