package com.manosgrigorakis.logisticsplatform.payments.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.poi.ss.usermodel.IndexedColors;

@Getter
public enum InvoiceStatus {
    @JsonProperty("paid")
    PAID("Πληρωμένη", IndexedColors.LIGHT_GREEN),

    @JsonProperty("disputed")
    DISPUTED("Σε διαφωνία", IndexedColors.LIGHT_ORANGE),

    @JsonProperty("outstanding")
    OUTSTANDING("Εκκρεμής", IndexedColors.ROSE),

    @JsonProperty("partially_paid")
    PARTIALLY_PAID("Μερικώς Πληρωμένη", IndexedColors.LIGHT_YELLOW);

    private final String greekTranslate;
    private final IndexedColors excelBackgroundColor;

    InvoiceStatus(String greekTranslate, IndexedColors excelBackgroundColor) {
        this.greekTranslate = greekTranslate;
        this.excelBackgroundColor = excelBackgroundColor;
    }
}
