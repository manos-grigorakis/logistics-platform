package com.manosgrigorakis.logisticsplatform.analytics.controller;

import com.manosgrigorakis.logisticsplatform.analytics.dto.ValueByStatus;
import com.manosgrigorakis.logisticsplatform.analytics.service.AnalyticsService;
import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
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
@RequestMapping("${app.api.prefix}/v1/analytics")
@RestController
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/total-customers")
    public ApiResponseWrapper<ValueResponse<Long>> getTotalCustomers(){
        return new ApiResponseWrapper<>(analyticsService.getTotalCustomers());
    }

    @GetMapping("/total-shipments")
    public ApiResponseWrapper<ValueResponse<Long>> getTotalShipments(){
        return new ApiResponseWrapper<>(analyticsService.getTotalShipments());
    }

    @GetMapping("/total-outstanding-amount")
    public ApiResponseWrapper<ValueResponse<BigDecimal>> getTotalOutstandingInvoicesAmount(){
        return new ApiResponseWrapper<>(analyticsService.getTotalOutstandingInvoicesAmount());
    }

    @GetMapping("/total-pending-shipments")
    public ApiResponseWrapper<ValueResponse<Integer>> getUpcomingPendingShipments(){
        return new ApiResponseWrapper<>(analyticsService.getUpcomingPendingShipments());
    }

    @GetMapping("/quotes-by-status")
    public ApiResponseWrapper<List<ValueByStatus<QuoteStatus>>> getQuotesByStatus(){
        return new ApiResponseWrapper<>(analyticsService.getQuotesByStatus());
    }

    @GetMapping("/shipments-by-status")
    public ApiResponseWrapper<List<ValueByStatus<ShipmentStatus>>> getShipmentsByStatus(){
        return new ApiResponseWrapper<>(analyticsService.getShipmentByStatus());
    }

    @GetMapping("/invoices-by-status")
    public ApiResponseWrapper<List<ValueByStatus<InvoiceStatus>>> getInvoicesByStatus(){
        return new ApiResponseWrapper<>(analyticsService.getInvoicesByStatus());
    }
}
