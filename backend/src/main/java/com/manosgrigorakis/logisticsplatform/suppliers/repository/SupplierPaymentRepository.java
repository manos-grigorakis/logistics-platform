package com.manosgrigorakis.logisticsplatform.suppliers.repository;

import com.manosgrigorakis.logisticsplatform.suppliers.model.SupplierPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierPaymentRepository extends JpaRepository<SupplierPayment, Long> {
    @Query("SELECT sp FROM SupplierPayment AS sp " +
            "WHERE (:number IS NULL OR UPPER(sp.number) LIKE UPPER(CONCAT('%', :number, '%')))")
    Page<SupplierPayment> findAllByNumber(@Param("number") String number, Pageable pageable);

    @Query("SELECT sp.number FROM SupplierPayment AS sp WHERE Year(sp.createdAt) = :year ORDER BY sp.id DESC LIMIT 1")
    Optional<String> findLastSupplierPaymentByYear(@Param("year") int year);
}
