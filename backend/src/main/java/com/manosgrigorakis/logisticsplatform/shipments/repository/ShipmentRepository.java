package com.manosgrigorakis.logisticsplatform.shipments.repository;

import com.manosgrigorakis.logisticsplatform.analytics.dto.ValueByStatus;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long>, JpaSpecificationExecutor<Shipment> {
    @Query("SELECT s.number FROM Shipment AS s " +
            "WHERE Year(s.createdAt) = :year " +
            "ORDER BY s.id DESC LIMIT 1")
    Optional<String> findLastShipmentNumberByYear(@Param("year") int year);

    boolean existsByQuoteId(Long id);

    Page<Shipment> findShipmentByDriverId(Long driverId, Pageable pageable);

    Integer countAllByStatusAndPickupBefore(ShipmentStatus status, LocalDateTime pickup);

    @Query("SELECT s.status, COUNT(s) FROM Shipment AS s GROUP BY s.status")
    List<ValueByStatus<ShipmentStatus>> getShipmentsByStatus();
}
