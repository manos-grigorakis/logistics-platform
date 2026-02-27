package com.manosgrigorakis.logisticsplatform.audit.repository;

import com.manosgrigorakis.logisticsplatform.audit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {
}
