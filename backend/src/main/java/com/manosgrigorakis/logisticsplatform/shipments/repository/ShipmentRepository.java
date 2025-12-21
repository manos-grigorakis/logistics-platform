package com.manosgrigorakis.logisticsplatform.shipments.repository;

import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    @Query("SELECT s.number FROM Shipment AS s " +
            "WHERE Year(s.createdAt) = :year " +
            "ORDER BY s.id DESC LIMIT 1")
    Optional<String> findLastShipmentNumberByYear(@Param("year") int year);

    boolean existsByQuoteId(Long id);
}
