package com.manosgrigorakis.logisticsplatform.service;

public interface FileStorageService {
    void store(String key, byte[] content, String contentType);

    String createPresignedUrl(String key);
}
