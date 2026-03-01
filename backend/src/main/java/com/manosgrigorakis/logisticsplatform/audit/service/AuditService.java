package com.manosgrigorakis.logisticsplatform.audit.service;

import com.manosgrigorakis.logisticsplatform.audit.dto.AuditEventDTO;

public interface AuditService {
    void log(AuditEventDTO event);
}
