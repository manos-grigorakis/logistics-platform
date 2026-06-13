package com.manosgrigorakis.logisticsplatform.suppliers.controller;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.*;
import com.manosgrigorakis.logisticsplatform.suppliers.service.SupplierPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/v1/supplier-payments")
@RestController
public class SupplierPaymentController {
    private final SupplierPaymentService supplierPaymentService;

    @GetMapping
    public ApiResponseWrapper<Page<SupplierPaymentResponse>> getAllSupplierPayments(
            @ParameterObject @ModelAttribute SupplierPaymentFilterRequest filterRequest,
            @ParameterObject @ModelAttribute @Valid PageFilterRequest pageRequest,
            @ParameterObject @ModelAttribute @Valid SortFilterRequest sortRequest) {
        return new ApiResponseWrapper<>(
                supplierPaymentService.getSupplierPayments(filterRequest, pageRequest, sortRequest));
    }

    @GetMapping("/{id}")
    public ApiResponseWrapper<SupplierPaymentResponse> getSupplierPaymentById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(supplierPaymentService.getSupplierPaymentById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseWrapper<SupplierPaymentResponse> createSupplierPayment(
            @ModelAttribute @Valid SupplierPaymentCreateRequest request) {
        return new ApiResponseWrapper<>(supplierPaymentService.createSupplierPayment(request));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseWrapper<SupplierPaymentResponse> updateSupplierPaymentById(
            @PathVariable Long id, @ModelAttribute @Valid SupplierPaymentUpdateRequest request) {
        return new ApiResponseWrapper<>(supplierPaymentService.updateSupplierPaymentById(id, request));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/status")
    public void updateSupplierPaymentStatusById(@PathVariable Long id,
                                                @RequestBody @Valid SupplierPaymentStatusUpdateRequest request) {
        supplierPaymentService.updateSupplierPaymentStatusById(id, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSupplierPaymentById(@PathVariable Long id) {
        supplierPaymentService.deleteSupplierPaymentById(id);
    }
}
