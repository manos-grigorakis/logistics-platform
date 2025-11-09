package com.manosgrigorakis.logisticsplatform.exception;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
    private Object details;
}
