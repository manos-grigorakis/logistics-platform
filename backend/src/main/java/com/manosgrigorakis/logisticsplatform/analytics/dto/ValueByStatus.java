package com.manosgrigorakis.logisticsplatform.analytics.dto;

public record ValueByStatus<T>(T status, Long count) {
}
