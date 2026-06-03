package com.manosgrigorakis.logisticsplatform.analytics.controller;

import com.manosgrigorakis.logisticsplatform.analytics.dto.ValueByStatus;
import com.manosgrigorakis.logisticsplatform.analytics.service.AnalyticsService;
import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponse;
import com.manosgrigorakis.logisticsplatform.common.dto.ValueResponse;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/analytics")
@RestController
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/total-customers")
    public ApiResponse<ValueResponse<Long>> getTotalCustomers(){
        return new ApiResponse<>(analyticsService.getTotalCustomers());
    }

    @GetMapping("/total-shipments")
    public ApiResponse<ValueResponse<Long>> getTotalShipments(){
        return new ApiResponse<>(analyticsService.getTotalShipments());
    }

    @GetMapping("/total-outstanding-amount")
    public ApiResponse<ValueResponse<BigDecimal>> getTotalOutstandingInvoicesAmount(){
        return new ApiResponse<>(analyticsService.getTotalOutstandingInvoicesAmount());
    }

    @GetMapping("/total-pending-shipments")
    public ApiResponse<ValueResponse<Integer>> getUpcomingPendingShipments(){
        return new ApiResponse<>(analyticsService.getUpcomingPendingShipments());
    }

    @GetMapping("/quotes-by-status")
    public ApiResponse<List<ValueByStatus<QuoteStatus>>> getQuotesByStatus(){
        return new ApiResponse<>(analyticsService.getQuotesByStatus());
    }

    @GetMapping("/shipments-by-status")
    public ApiResponse<List<ValueByStatus<ShipmentStatus>>> getShipmentsByStatus(){
        return new ApiResponse<>(analyticsService.getShipmentByStatus());
    }

    @GetMapping("/invoices-by-status")
    public ApiResponse<List<ValueByStatus<InvoiceStatus>>> getInvoicesByStatus(){
        return new ApiResponse<>(analyticsService.getInvoicesByStatus());
    }
}
