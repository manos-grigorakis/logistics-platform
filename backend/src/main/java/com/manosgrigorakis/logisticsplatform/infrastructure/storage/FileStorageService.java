package com.manosgrigorakis.logisticsplatform.infrastructure.storage;

public interface FileStorageService {
    void store(String key, byte[] content, String contentType);

    void deleteObject(String key);

    String createPresignedUrl(String key);
}
