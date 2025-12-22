package com.manosgrigorakis.logisticsplatform.shipments.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ShipmentStatus {
    @JsonProperty("pending")
    PENDING("pending", true, false),

    @JsonProperty("dispatched")
    DISPATCHED("dispatched", false, false),

    @JsonProperty("delivered")
    DELIVERED("delivered", false, true);

    private final String label;
    private final boolean editable;
    private final boolean finalized;

    ShipmentStatus(String label, boolean editable, boolean finalized) {
        this.label = label;
        this.editable = editable;
        this.finalized = finalized;
    }
}
