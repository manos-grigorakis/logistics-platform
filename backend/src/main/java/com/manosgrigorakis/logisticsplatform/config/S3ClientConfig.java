package com.manosgrigorakis.logisticsplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3ClientConfig {
    @Value("${app.minio.accessKey}")
    private String accessKey;

    @Value("${app.minio.secretKey}")
    private String secretKey;

    @Value("${app.minio.endpoint}")
    private String endpoint;

    @Value("${app.aws.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        Region s3Region = Region.of(region);

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(s3Region)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // required for minio
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        Region s3Region = Region.of(region);

        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(s3Region)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)  // required for minio
                        .build())
                .build();
    }
}
