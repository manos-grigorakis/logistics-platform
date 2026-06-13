package com.manosgrigorakis.logisticsplatform.suppliers.controller;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.service.SupplierPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/v1/supplier-payments")
@RestController
public class SupplierPaymentController {
    private final SupplierPaymentService supplierPaymentService;

    @GetMapping("/{id}")
    public ApiResponseWrapper<SupplierPaymentResponse> getSupplierPaymentById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(supplierPaymentService.getSupplierPaymentById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseWrapper<SupplierPaymentResponse> createSupplierPayment(
            @ModelAttribute @Valid SupplierPaymentRequest request) {
        return new ApiResponseWrapper<>(supplierPaymentService.createSupplierPayment(request));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSupplierPaymentById(@PathVariable Long id) {
        supplierPaymentService.deleteSupplierPaymentById(id);
    }
}
