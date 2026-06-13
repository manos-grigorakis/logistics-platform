package com.manosgrigorakis.logisticsplatform.suppliers.controller;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierListResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/v1/suppliers")
@RestController
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping
    public ApiResponseWrapper<Page<SupplierListResponse>> getAllSuppliers(
            @ParameterObject @ModelAttribute SupplierFilterRequest filterRequest,
            @ParameterObject @ModelAttribute PageFilterRequest pageRequest,
            @ParameterObject @ModelAttribute SortFilterRequest sortRequest) {
        return new ApiResponseWrapper<>(supplierService.findAllSuppliers(filterRequest, pageRequest, sortRequest));
    }

    @GetMapping("/{id}")
    public ApiResponseWrapper<SupplierResponse> getSupplierById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(supplierService.getSupplierById(id));
    }

    @PostMapping
    public ApiResponseWrapper<SupplierResponse> createSupplier(@RequestBody @Valid SupplierRequest supplierRequest) {
        return new ApiResponseWrapper<>(supplierService.createSupplier(supplierRequest));
    }

    @PutMapping("/{id}")
    public ApiResponseWrapper<SupplierResponse> updateSupplierById(@PathVariable Long id,
                                                                   @RequestBody @Valid SupplierRequest request) {
        return new ApiResponseWrapper<>(supplierService.updateSupplierById(id, request));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/deactivate")
    public void deactivateSupplierById(@PathVariable Long id) {
        supplierService.deactivateSupplierById(id);
    }
}
