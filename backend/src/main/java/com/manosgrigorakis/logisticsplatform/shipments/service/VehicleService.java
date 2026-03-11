package com.manosgrigorakis.logisticsplatform.shipments.service;

import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleResponseDTO;

import java.util.List;

public interface VehicleService {
    List<VehicleResponseDTO> getAllVehicles();

    VehicleResponseDTO getVehicleById(Long id);

    VehicleResponseDTO createVehicle(VehicleRequestDTO dto);

    VehicleResponseDTO updateVehicleById(Long id, VehicleRequestDTO dto);

    void deleteVehicleById(Long id);
}
