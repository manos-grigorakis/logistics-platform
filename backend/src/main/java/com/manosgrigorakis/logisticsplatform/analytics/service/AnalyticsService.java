package com.manosgrigorakis.logisticsplatform.analytics.service;

import com.manosgrigorakis.logisticsplatform.analytics.dto.ValueByStatus;
import com.manosgrigorakis.logisticsplatform.common.dto.ValueResponse;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;

import java.math.BigDecimal;
import java.util.List;

public interface AnalyticsService {
    ValueResponse<Long> getTotalCustomers();

    ValueResponse<Long> getTotalShipments();

    ValueResponse<BigDecimal> getTotalOutstandingInvoicesAmount();

    ValueResponse<Integer> getUpcomingPendingShipments();

    List<ValueByStatus<QuoteStatus>> getQuotesByStatus();

    List<ValueByStatus<ShipmentStatus>> getShipmentByStatus();

    List<ValueByStatus<InvoiceStatus>> getInvoicesByStatus();
}
