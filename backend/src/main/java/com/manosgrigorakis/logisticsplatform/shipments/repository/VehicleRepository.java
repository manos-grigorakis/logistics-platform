package com.manosgrigorakis.logisticsplatform.shipments.repository;

import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
