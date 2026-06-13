package com.manosgrigorakis.logisticsplatform.common.exception;


import lombok.Getter;

@Getter
public class StorageServiceException extends RuntimeException {
    private String errorCode = "";

    public StorageServiceException(String message) {
        super(message);
    }

    public StorageServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
