package com.manosgrigorakis.logisticsplatform.payments.repository;

import com.manosgrigorakis.logisticsplatform.payments.model.InvoicePayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoicePaymentsRepository extends JpaRepository<InvoicePayments, Long> {
}
