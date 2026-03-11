package com.manosgrigorakis.logisticsplatform.shipments.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShipmentCargoUnit {
    @JsonProperty("pallet")
    PALLET,

    @JsonProperty("box")
    BOX,

    @JsonProperty("piece")
    PIECE,

    @JsonProperty("roll")
    ROLL,

    @JsonProperty("bag")
    BAG
}
