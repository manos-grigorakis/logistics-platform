package com.manosgrigorakis.logisticsplatform.payments.repository;

import com.manosgrigorakis.logisticsplatform.payments.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByExternalInvoiceNumber(String number);
}
