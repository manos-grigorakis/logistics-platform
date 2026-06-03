package com.manosgrigorakis.logisticsplatform.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiResponse<T>(String transactionId, T data, LocalDateTime timestamp, ErrorResponse error) {
    public ApiResponse {
        if (transactionId == null) transactionId = UUID.randomUUID().toString();
        if (timestamp == null) timestamp = LocalDateTime.now();
    }

    public ApiResponse(T data) {
        this(null, data, null, null);
    }

    public ApiResponse(ErrorResponse error) {
        this(null, null, null, error);
    }
}
