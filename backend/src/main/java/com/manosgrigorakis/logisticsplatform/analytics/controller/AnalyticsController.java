package com.manosgrigorakis.logisticsplatform.analytics.controller;

import com.manosgrigorakis.logisticsplatform.analytics.dto.ValueByStatus;
import com.manosgrigorakis.logisticsplatform.analytics.service.AnalyticsService;
import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.common.dto.ValueResponse;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Analytics", description = "Dashboard metrics and aggregated business insights")
@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/v1/analytics")
@RestController
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @Operation(summary = "Get Total Customers", description = "Returns total number of registered customers")
    @ApiResponse(responseCode = "200", description = "Count of customers")
    @GetMapping("/total-customers")
    public ApiResponseWrapper<ValueResponse<Long>> getTotalCustomers(){
        return new ApiResponseWrapper<>(analyticsService.getTotalCustomers());
    }

    @Operation(summary = "Get Total Shipments", description = "Returns total number of shipments")
    @ApiResponse(responseCode = "200", description = "Count of shipments")
    @GetMapping("/total-shipments")
    public ApiResponseWrapper<ValueResponse<Long>> getTotalShipments(){
        return new ApiResponseWrapper<>(analyticsService.getTotalShipments());
    }

    @Operation(summary = "Get Total Outstanding Invoices Amount", description =
            "Returns the total remaining unpaid amount for invoices marked as OUTSTANDING or PARTIALLY_PAID")
    @ApiResponse(responseCode = "200", description = "Total outstanding invoices amount")
    @GetMapping("/total-outstanding-amount")
    public ApiResponseWrapper<ValueResponse<BigDecimal>> getTotalOutstandingInvoicesAmount(){
        return new ApiResponseWrapper<>(analyticsService.getTotalOutstandingInvoicesAmount());
    }

    @Operation(summary = "Get Upcoming Pending Shipments Count",
            description = "Returns the total number of pending shipments scheduled until tomorrow")
    @ApiResponse(responseCode = "200", description = "Upcoming pending shipments count")
    @GetMapping("/total-pending-shipments")
    public ApiResponseWrapper<ValueResponse<Integer>> getUpcomingPendingShipments(){
        return new ApiResponseWrapper<>(analyticsService.getUpcomingPendingShipments());
    }

    @Operation(summary = "Get Quotes Grouped by Status",
            description = "Returns aggregated quotes statistics grouped by status")
    @ApiResponse(responseCode = "200", description = "Found quotes grouped by status")
    @GetMapping("/quotes-by-status")
    public ApiResponseWrapper<List<ValueByStatus<QuoteStatus>>> getQuotesByStatus(){
        return new ApiResponseWrapper<>(analyticsService.getQuotesByStatus());
    }

    @Operation(summary = "Get Shipments Grouped by Status",
            description = "Returns aggregated shipments statistics grouped by status")
    @ApiResponse(responseCode = "200", description = "Found shipments grouped by status")
    @GetMapping("/shipments-by-status")
    public ApiResponseWrapper<List<ValueByStatus<ShipmentStatus>>> getShipmentsByStatus(){
        return new ApiResponseWrapper<>(analyticsService.getShipmentsByStatus());
    }

    @Operation(summary = "Get Invoices Grouped by Status",
            description = "Returns aggregated invoices statistics grouped by status")
    @ApiResponse(responseCode = "200", description = "Found invoices grouped by status")
    @GetMapping("/invoices-by-status")
    public ApiResponseWrapper<List<ValueByStatus<InvoiceStatus>>> getInvoicesByStatus(){
        return new ApiResponseWrapper<>(analyticsService.getInvoicesByStatus());
    }
}
