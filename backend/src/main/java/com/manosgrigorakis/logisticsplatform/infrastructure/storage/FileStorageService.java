package com.manosgrigorakis.logisticsplatform.infrastructure.storage;

public interface FileStorageService {
    void store(String key, byte[] content, String contentType);

    String createPresignedUrl(String key);
}
