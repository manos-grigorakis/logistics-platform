package com.manosgrigorakis.logisticsplatform.payments.repository;

import com.manosgrigorakis.logisticsplatform.analytics.dto.ValueByStatus;
import com.manosgrigorakis.logisticsplatform.common.dto.ValueResponse;
import com.manosgrigorakis.logisticsplatform.payments.enums.InvoiceStatus;
import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByExternalInvoiceNumber(String number);

    @Query("SELECT SUM(i.remainingAmount) " +
            "FROM Invoice AS i " +
            "WHERE i.status IN :statuses")
    BigDecimal totalOutstandingInvoicesAmount(@Param("statuses") List<InvoiceStatus> statuses);

    @Query("SELECT i.status, COUNT(i) FROM Invoice AS i GROUP BY i.status")
    List<ValueByStatus<InvoiceStatus>> getInvoicesByStatus();

}
