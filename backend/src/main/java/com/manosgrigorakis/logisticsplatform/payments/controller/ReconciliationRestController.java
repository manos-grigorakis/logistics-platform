package com.manosgrigorakis.logisticsplatform.payments.controller;

import com.manosgrigorakis.logisticsplatform.payments.dto.ReconciliationRequestDTO;
import com.manosgrigorakis.logisticsplatform.payments.service.ReconciliationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reconciliation")
@Tag(name = "Reconciliation")
public class ReconciliationRestController {
    private final ReconciliationService reconciliationService;

    public ReconciliationRestController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> reconciliation(@ModelAttribute @Valid ReconciliationRequestDTO dto) {
        this.reconciliationService.reconciliationProcess(dto);
        return ResponseEntity.noContent().build();
    }
}
