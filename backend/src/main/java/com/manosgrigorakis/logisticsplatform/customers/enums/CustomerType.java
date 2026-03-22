package com.manosgrigorakis.logisticsplatform.customers.enums;

import lombok.Getter;

@Getter
public enum CustomerType {
    INDIVIDUAL("Ιδιώτης"),
    COMPANY("Εταιρεία");


    private final String greekTranslate;

    CustomerType(String greekTranslate) {
        this.greekTranslate = greekTranslate;
    }
}
