package com.manosgrigorakis.logisticsplatform.audit.service;

import com.manosgrigorakis.logisticsplatform.audit.dto.AuditEventDTO;
import com.manosgrigorakis.logisticsplatform.audit.model.AuditLog;
import com.manosgrigorakis.logisticsplatform.audit.repository.AuditRepository;
import com.manosgrigorakis.logisticsplatform.common.utils.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository;
    private final ClientInfo clientInfo;
    private final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    public AuditServiceImpl(AuditRepository auditRepository, ClientInfo clientInfo) {
        this.auditRepository = auditRepository;
        this.clientInfo = clientInfo;
    }

    @Override
    public void log(AuditEventDTO event) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .entityType(event.entityType())
                    .entityId(event.entityId())
                    .action(event.action())
                    .changes(event.changes())
                    .notes(event.notes())
                    .ipAddress(this.clientInfo.getClientIp())
                    .userAgent(this.clientInfo.getUserAgent())
                    .build();

            auditLog.setUser(event.user());

            this.auditRepository.save(auditLog);
        } catch (Exception e) {
            log.warn("Audit logging failed", e);
        }

    }
}
