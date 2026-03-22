package com.manosgrigorakis.logisticsplatform.payments.repository;

import com.manosgrigorakis.logisticsplatform.payments.model.ReconciliationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReconciliationReportRepository extends JpaRepository<ReconciliationReport, Long> {
}
