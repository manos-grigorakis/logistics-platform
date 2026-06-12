package com.manosgrigorakis.logisticsplatform.suppliers.repository;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierListResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query("SELECT s.id, s.companyName, " +
            "sum(sp.totalAmount) as totalAmount, sum(sp.totalAmount - sp.paidAmount) as remainingAmount " +
            "FROM Supplier AS s " + "LEFT JOIN s.supplierPayments AS sp " +
            "WHERE(:companyName IS NULL OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))) " +
            "GROUP BY s.id, s.companyName")
    Page<SupplierListResponse> findSupplierWithTotals(@Param("companyName") String companyName, Pageable pageable);

    Optional<Supplier> findByCompanyName(String companyName);

    boolean existsByCompanyName(String companyName);
}
