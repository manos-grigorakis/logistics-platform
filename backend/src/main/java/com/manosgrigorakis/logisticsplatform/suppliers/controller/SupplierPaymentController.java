package com.manosgrigorakis.logisticsplatform.suppliers.controller;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.*;
import com.manosgrigorakis.logisticsplatform.suppliers.service.SupplierPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Supplier Payments", description =
        "CRUD operations for suppliers payments with presigned URLs for uploaded files")
@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/v1/supplier-payments")
@RestController
public class SupplierPaymentController {
    private final SupplierPaymentService supplierPaymentService;

    @Operation(summary = "Get all Supplier Payments", description = "Lists all supplier payments with pagination")
    @ApiResponse(responseCode = "200", description = "List of supplier payments with pagination")
    @GetMapping
    public ApiResponseWrapper<Page<SupplierPaymentResponse>> getAllSupplierPayments(
            @ParameterObject @ModelAttribute SupplierPaymentFilterRequest filterRequest,
            @ParameterObject @ModelAttribute @Valid PageFilterRequest pageRequest,
            @ParameterObject @ModelAttribute SortFilterRequest sortRequest) {
        return new ApiResponseWrapper<>(
                supplierPaymentService.getSupplierPayments(filterRequest, pageRequest, sortRequest));
    }

    @Operation(summary = "Get Supplier Payment by Id",
            description = "Find supplier payment by id and generate presigned preview URL for uploaded files if exist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded supplier payment"),
            @ApiResponse(responseCode = "404", description = "Supplier payment not found"),
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<SupplierPaymentResponse> getSupplierPaymentById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(supplierPaymentService.getSupplierPaymentById(id));
    }

    @Operation(summary = "Create a new Supplier Payment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Supplier payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Field validation"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Supplier is inactive or Paid amount exceeds total amount"),
            @ApiResponse(responseCode = "503", description = "Storage error or DB error"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseWrapper<SupplierPaymentResponse> createSupplierPayment(
            @ModelAttribute @Valid SupplierPaymentCreateRequest request) {
        return new ApiResponseWrapper<>(supplierPaymentService.createSupplierPayment(request));
    }

    @Operation(summary = "Update a Supplier Payment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated supplier payment"),
            @ApiResponse(responseCode = "400", description = "Field validation"),
            @ApiResponse(responseCode = "404", description = "Supplier payment not found"),
            @ApiResponse(responseCode = "409", description = "Supplier is inactive or Paid amount exceeds total amount"),
            @ApiResponse(responseCode = "503", description = "Storage error or DB error"),
    })
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseWrapper<SupplierPaymentResponse> updateSupplierPaymentById(
            @PathVariable Long id, @ModelAttribute @Valid SupplierPaymentUpdateRequest request) {
        return new ApiResponseWrapper<>(supplierPaymentService.updateSupplierPaymentById(id, request));
    }

    @Operation(summary = "Update a Supplier Payment Status by id", description =
            "Update the current supplier payment status to the desired one")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Supplier payment status updated"),
            @ApiResponse(responseCode = "400", description = "Field validation"),
            @ApiResponse(responseCode = "404", description = "Supplier payment not found"),
            @ApiResponse(responseCode = "409", description = """
                    **Possible Causes:**
                    - Supplier is inactive
                    - Paid amount exceeds total amount
                    - Violation on status transition
                    """
            ),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/status")
    public void updateSupplierPaymentStatusById(@PathVariable Long id,
                                                @RequestBody @Valid SupplierPaymentStatusUpdateRequest request) {
        supplierPaymentService.updateSupplierPaymentStatusById(id, request);
    }
}
