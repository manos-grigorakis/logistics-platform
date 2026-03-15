package com.manosgrigorakis.logisticsplatform.payments.repository;

import com.manosgrigorakis.logisticsplatform.payments.model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
}
