package com.manosgrigorakis.logisticsplatform.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum QuoteItemUnit {
    @JsonProperty("hour")
    HOUR("hour", "Ωρα", "Hour"),

    @JsonProperty("piece")
    PIECE("piece", "Τεμάχια", "Piece"),

    @JsonProperty("pallet")
    PALLET("pallet", "Παλέτα", "Pallet");

    private final String code;
    private final String displayNameEl;
    private final String displayNameEn;

    QuoteItemUnit(String code, String displayNameEl, String displayNameEn) {
        this.code = code;
        this.displayNameEl = displayNameEl;
        this.displayNameEn = displayNameEn;
    }

    public String getDisplayName(String locale) {
        return "en".equals(locale) ? displayNameEn : displayNameEl;
    }
}
