package com.manosgrigorakis.logisticsplatform.audit.service;

import com.manosgrigorakis.logisticsplatform.audit.enums.AuditAction;
import com.manosgrigorakis.logisticsplatform.audit.model.AuditLog;
import com.manosgrigorakis.logisticsplatform.audit.repository.AuditRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository;

    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void logLoginFailed(String reason, String email, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .entityType("User")
                .action(AuditAction.LOGIN_FAILED)
                .notes("Reason: " + reason + " - Email: " + email)
                .ipAddress(ipAddress)
                .build();

        this.auditRepository.save(auditLog);
    }

    @Override
    public void logPasswordReset(Long userId, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .entityType("User")
                .entityId(userId)
                .action(AuditAction.PASSWORD_RESET)
                .ipAddress(ipAddress)
                .build();

        this.auditRepository.save(auditLog);
    }
}
