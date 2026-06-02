package com.manosgrigorakis.logisticsplatform.analytics.controller;

import com.manosgrigorakis.logisticsplatform.analytics.dto.ValueByStatus;
import com.manosgrigorakis.logisticsplatform.analytics.service.AnalyticsService;
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
    public ValueResponse<Long> getTotalCustomers(){
        return analyticsService.getTotalCustomers();
    }

    @GetMapping("/total-shipments")
    public ValueResponse<Long> getTotalShipments(){
        return analyticsService.getTotalShipments();
    }

    @GetMapping("/total-outstanding-amount")
    public ValueResponse<BigDecimal> getTotalOutstandingInvoicesAmount(){
        return analyticsService.getTotalOutstandingInvoicesAmount();
    }

    @GetMapping("/total-pending-shipments")
    public ValueResponse<Integer> getUpcomingPendingShipments(){
        return analyticsService.getUpcomingPendingShipments();
    }

    @GetMapping("/quotes-by-status")
    public List<ValueByStatus<QuoteStatus>> getQuotesByStatus(){
        return analyticsService.getQuotesByStatus();
    }

    @GetMapping("/shipments-by-status")
    public List<ValueByStatus<ShipmentStatus>> getShipmentsByStatus(){
        return analyticsService.getShipmentByStatus();
    }

    @GetMapping("/invoices-by-status")
    public List<ValueByStatus<InvoiceStatus>> getInvoicesByStatus(){
        return analyticsService.getInvoicesByStatus();
    }
}
