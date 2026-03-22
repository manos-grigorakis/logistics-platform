package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.payments.dto.CreateReconciliationReport;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationReportCreateResponseDTO;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationReportResponseDTO;

public interface ReconciliationReportService {
    ReconciliationReportResponseDTO getReconciliationReport(Long id);

    ReconciliationReportCreateResponseDTO createReconciliationReport(CreateReconciliationReport dto);
}
