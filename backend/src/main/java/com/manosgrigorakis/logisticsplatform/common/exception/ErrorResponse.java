package com.manosgrigorakis.logisticsplatform.common.exception;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
    private String errorCode;
    private Object details;

    public ErrorResponse(int status, String message, long timestamp, Object details) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }
}
