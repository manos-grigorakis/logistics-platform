package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.payments.dto.CreateReconciliationReport;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationReportCreateResponseDTO;

public interface ReconciliationReportService {
    ReconciliationReportCreateResponseDTO createReconciliationReport(CreateReconciliationReport dto);
}
