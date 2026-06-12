package com.manosgrigorakis.logisticsplatform.suppliers.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SupplierPaymentType {
    @JsonProperty("fuel")
    FUEL,

    @JsonProperty("insurance")
    INSURANCE,

    @JsonProperty("service")
    SERVICE,

    @JsonProperty("other")
    OTHER
}
