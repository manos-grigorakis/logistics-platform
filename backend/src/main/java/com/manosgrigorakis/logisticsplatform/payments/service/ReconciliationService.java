package com.manosgrigorakis.logisticsplatform.payments.service;

import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationProcessResponse;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationRequestDTO;

public interface ReconciliationService {
    ReconciliationProcessResponse reconciliationProcess(ReconciliationRequestDTO dto);
}
