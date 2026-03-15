package com.manosgrigorakis.logisticsplatform.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class BadRequestException extends RuntimeException {
    private final Map<String, Object> details;
    private final String errorCode;

    public BadRequestException(String message) {
        super(message);
        this.details = Map.of();
        this.errorCode = "";
    }

    public BadRequestException(String message, String errorCode, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode != null ? errorCode :  "";
        this.details = details != null ? Map.copyOf(details) : Map.of();
    }

    public BadRequestException(String message, Map<String, Object> details) {
        super(message);
        this.details = details != null ? Map.copyOf(details) : Map.of();
        this.errorCode = "";
    }

    public BadRequestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode != null ? errorCode :  "";
        this.details = Map.of();
    }


}
