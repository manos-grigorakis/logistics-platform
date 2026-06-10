package com.manosgrigorakis.logisticsplatform.common.exception;

import lombok.Getter;

@Getter
public class DocumentProcessingException extends RuntimeException {
    private String errorCode = "";

    public DocumentProcessingException(String message) {
        super(message);
    }

    public DocumentProcessingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
