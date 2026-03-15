package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationRequestDTO;

public interface ReconciliationService {
    void reconciliationProcess(ReconciliationRequestDTO dto);
}
