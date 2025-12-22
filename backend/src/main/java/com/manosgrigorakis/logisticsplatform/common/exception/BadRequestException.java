package com.manosgrigorakis.logisticsplatform.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class BadRequestException extends RuntimeException {
    private final Map<String, Object> details;

    public BadRequestException(String message) {
        super(message);
        this.details = Map.of();
    }

    public BadRequestException(String message, Map<String, Object> details) {
        super(message);

        if(details != null) {
            this.details = Map.copyOf(details);
        } else {
            this.details = Map.of();
        }
    }
}
