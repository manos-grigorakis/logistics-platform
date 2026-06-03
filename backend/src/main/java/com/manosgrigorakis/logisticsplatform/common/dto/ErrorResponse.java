package com.manosgrigorakis.logisticsplatform.common.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String errorCode;
    private Object details;

    public ErrorResponse(int status, String message, Object details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }
}
