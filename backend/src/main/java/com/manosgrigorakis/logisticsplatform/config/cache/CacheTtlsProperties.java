package com.manosgrigorakis.logisticsplatform.config.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties(prefix = "app.cache")
public record CacheTtlsProperties(Map<String, Duration> ttls) {
    public CacheTtlsProperties(Map<String, Duration> ttls) {
        this.ttls = ttls != null ? ttls : Map.of();
    }
}
