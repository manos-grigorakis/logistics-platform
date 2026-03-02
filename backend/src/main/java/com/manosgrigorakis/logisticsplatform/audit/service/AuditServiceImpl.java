package com.manosgrigorakis.logisticsplatform.audit.service;

import com.manosgrigorakis.logisticsplatform.audit.dto.AuditEventDTO;
import com.manosgrigorakis.logisticsplatform.audit.model.AuditLog;
import com.manosgrigorakis.logisticsplatform.audit.repository.AuditRepository;
import com.manosgrigorakis.logisticsplatform.common.utils.ClientInfo;
import com.manosgrigorakis.logisticsplatform.security.CurrentUser;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository;
    private final ClientInfo clientInfo;
    private final CurrentUser currentUser;
    private final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    public AuditServiceImpl(AuditRepository auditRepository, ClientInfo clientInfo, CurrentUser currentUser) {
        this.auditRepository = auditRepository;
        this.clientInfo = clientInfo;
        this.currentUser = currentUser;
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

            Long userId = currentUser.getLoggedInUser();

            if(userId != null) {
                // Sets the foreign key without loading entire User entity,
                // and without executing extra DB query
                auditLog.setUser(this.entityManager.getReference(User.class, userId));
            }

            this.auditRepository.save(auditLog);
        } catch (Exception e) {
            log.warn("Audit logging failed", e);
        }

    }
}
