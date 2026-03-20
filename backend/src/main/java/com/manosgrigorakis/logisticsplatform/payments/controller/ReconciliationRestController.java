package com.manosgrigorakis.logisticsplatform.payments.controller;

import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationProcessResponse;
import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.service.ReconciliationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "Reconciliation")
@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationRestController {
    private final ReconciliationService reconciliationService;

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReconciliationProcessResponse reconciliationProcess(@ModelAttribute @Valid ReconciliationRequestDTO dto) {
        return reconciliationService.reconciliationProcess(dto);
    }
}
