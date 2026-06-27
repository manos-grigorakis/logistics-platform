package com.manosgrigorakis.logisticsplatform.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(CacheTtlsProperties.class)
@Configuration
public class CaffeineConfig {
    @Bean
    public Caffeine<Object, Object> defaultCaffeineSpec() {
        return Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine, CacheTtlsProperties cacheTtls) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);

        cacheTtls.ttls().forEach((key, ttl) -> {
            caffeineCacheManager.registerCustomCache(key, Caffeine.newBuilder().expireAfterWrite(ttl).build());
        });

        return caffeineCacheManager;
    }
}
