package com.manosgrigorakis.logisticsplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableCaching
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
public class LogisticsPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogisticsPlatformApplication.class, args);
	}
}
