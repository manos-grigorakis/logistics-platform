package com.manosgrigorakis.logisticsplatform.common.exception;

import lombok.Getter;

@Getter
public class DuplicateEntryException extends RuntimeException {
    private String field;
    private String value;
    private String errorCode = "";

    public DuplicateEntryException(String message) {
        super(message);
    }

    public DuplicateEntryException(String field, String value) {
        super("Duplicate entry for " + field + ": " + value);
        this.field = field;
        this.value = value;
    }

    public DuplicateEntryException(String field, String value, String errorCode) {
        super("Duplicate entry for " + field + ": " + value);
        this.field = field;
        this.value = value;
        this.errorCode = errorCode;
    }

//    public static DuplicateEntryException withMessage(String message, String errorCode) {
//        DuplicateEntryException ex = new DuplicateEntryException(message);
//        ex.errorCode = errorCode;
//        return ex;
//    }
}
