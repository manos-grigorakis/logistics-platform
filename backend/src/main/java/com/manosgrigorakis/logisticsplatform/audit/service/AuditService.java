package com.manosgrigorakis.logisticsplatform.audit.service;

public interface AuditService {
    void logLoginFailed(String reason, String email, String ipAddress);

    void logPasswordReset(Long userId, String ipAddress);
}
