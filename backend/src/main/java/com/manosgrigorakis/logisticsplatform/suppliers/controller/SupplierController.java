package com.manosgrigorakis.logisticsplatform.suppliers.controller;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierListResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Suppliers", description = "CRUD operations for suppliers")
@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/v1/suppliers")
@RestController
public class SupplierController {
    private final SupplierService supplierService;

    @Operation(summary = "Get all Suppliers", description = "Lists all active suppliers with pagination")
    @ApiResponse(responseCode = "200", description = "List of suppliers with pagination")
    @GetMapping
    public ApiResponseWrapper<Page<SupplierListResponse>> getAllSuppliers(
            @ParameterObject @ModelAttribute SupplierFilterRequest filterRequest,
            @ParameterObject @ModelAttribute @Valid PageFilterRequest pageRequest,
            @ParameterObject @ModelAttribute SortFilterRequest sortRequest) {
        return new ApiResponseWrapper<>(supplierService.findAllSuppliers(filterRequest, pageRequest, sortRequest));
    }

    @Operation(summary = "Get Supplier by Id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded supplier"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<SupplierResponse> getSupplierById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(supplierService.getSupplierById(id));
    }

    @Operation(summary = "Create a new Supplier")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Supplier created successfully"),
            @ApiResponse(responseCode = "409", description = "Supplier already exists by company name"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponseWrapper<SupplierResponse> createSupplier(@RequestBody @Valid SupplierRequest supplierRequest) {
        return new ApiResponseWrapper<>(supplierService.createSupplier(supplierRequest));
    }

    @Operation(summary = "Update a Supplier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Supplier already exists by company name"),
    })
    @PutMapping("/{id}")
    public ApiResponseWrapper<SupplierResponse> updateSupplierById(@PathVariable Long id,
                                                                   @RequestBody @Valid SupplierRequest request) {
        return new ApiResponseWrapper<>(supplierService.updateSupplierById(id, request));
    }

    @Operation(summary = "Deactivate a Supplier by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supplier deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/deactivate")
    public void deactivateSupplierById(@PathVariable Long id) {
        supplierService.deactivateSupplierById(id);
    }
}
