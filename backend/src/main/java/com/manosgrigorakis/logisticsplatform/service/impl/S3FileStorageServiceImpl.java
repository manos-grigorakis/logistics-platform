package com.manosgrigorakis.logisticsplatform.service.impl;

import com.manosgrigorakis.logisticsplatform.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3FileStorageServiceImpl implements FileStorageService {
    private static final Logger log = LoggerFactory.getLogger(S3FileStorageServiceImpl.class);

    private final S3Client s3Client;

    @Value("${app.minio.bucketName}")
    private String bucketName;

    public S3FileStorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void store(String key, byte[] content, String contentType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(content));

            log.info("Stored file: {}", key);
        } catch (S3Exception e) {
            log.error("S3 error while storing file: {}", key, e);
            throw  new RuntimeException("Storage service error", e);
        } catch (SdkClientException e) {
            log.error("SDK client error while storing file: {}", key, e);
            throw  new RuntimeException("Storage client error", e);
        } catch (Exception e) {
            log.error("Error while storing file: {}", key, e);
            throw new RuntimeException("Error while storing file", e);
        }

    }
}
