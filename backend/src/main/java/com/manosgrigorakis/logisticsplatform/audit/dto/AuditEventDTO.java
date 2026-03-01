package com.manosgrigorakis.logisticsplatform.audit.dto;

import com.manosgrigorakis.logisticsplatform.audit.enums.AuditAction;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import lombok.Builder;

@Builder
public record AuditEventDTO(
        String entityType,
        Long entityId,
        AuditAction action,
        String changes,
        String notes,
        User user
)
{ }
