package com.manosgrigorakis.logisticsplatform.analytics.service;

import com.manosgrigorakis.logisticsplatform.analytics.dto.ValueByStatus;
import com.manosgrigorakis.logisticsplatform.common.dto.ValueResponse;
import com.manosgrigorakis.logisticsplatform.customers.repository.CustomerRepository;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.payments.repository.InvoiceRepository;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.quotes.repository.QuoteRepository;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import com.manosgrigorakis.logisticsplatform.shipments.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    private final CustomerRepository customerRepository;
    private final ShipmentRepository shipmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final QuoteRepository quoteRepository;

    @Cacheable(value = "analytics", key = "'total-customers'")
    @Override
    public ValueResponse<Long> getTotalCustomers() {
        return new ValueResponse<>(this.customerRepository.count());
    }

    @Cacheable(value = "analytics", key = "'total-shipments'")
    @Override
    public ValueResponse<Long> getTotalShipments() {
        return new ValueResponse<>(shipmentRepository.count());
    }

    @Cacheable(value = "analytics", key = "'total-outstanding-invoices-amount'")
    @Override
    public ValueResponse<BigDecimal> getTotalOutstandingInvoicesAmount() {
        return new ValueResponse<>(invoiceRepository.totalOutstandingInvoicesAmount(
                List.of(InvoiceStatus.OUTSTANDING, InvoiceStatus.PARTIALLY_PAID)));
    }

    @Cacheable(value = "analytics", key = "'upcoming-pending-shipments'")
    @Override
    public ValueResponse<Integer> getUpcomingPendingShipments() {
        return new ValueResponse<>(shipmentRepository.countAllByStatusAndPickupBefore(ShipmentStatus.PENDING,
                                                                                      LocalDateTime.now().plusDays(1)));
    }

    @Cacheable(value = "analytics", key = "'quotes-by-status'")
    @Override
    public List<ValueByStatus<QuoteStatus>> getQuotesByStatus() {
        return quoteRepository.getQuotesByStatus();
    }

    @Cacheable(value = "analytics", key = "'shipments-by-status'")
    @Override
    public List<ValueByStatus<ShipmentStatus>> getShipmentsByStatus() {
        return shipmentRepository.getShipmentsByStatus();
    }

    @Cacheable(value = "analytics", key = "'invoices-by-status'")
    @Override
    public List<ValueByStatus<InvoiceStatus>> getInvoicesByStatus() {
        return invoiceRepository.getInvoicesByStatus();
    }
}
