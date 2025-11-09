package com.manosgrigorakis.logisticsplatform.exception;

import lombok.Getter;

@Getter
public class DuplicateEntryException extends RuntimeException {
    private String field;
    private String value;

    public DuplicateEntryException(String message) {
        super(message);
    }

    public DuplicateEntryException(String field, String value) {
        super("Duplicate entry for " + field + ": " + value);
        this.field = field;
        this.value = value;
    }
}
