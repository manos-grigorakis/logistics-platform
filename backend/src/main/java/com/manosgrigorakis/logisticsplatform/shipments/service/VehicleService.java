package com.manosgrigorakis.logisticsplatform.shipments.service;

import com.manosgrigorakis.logisticsplatform.shipments.dto.VehicleRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.VehicleResponseDTO;

import java.util.List;

public interface VehicleService {
    List<VehicleResponseDTO> getAllVehicles();

    VehicleResponseDTO getVehicleById(Long id);

    VehicleResponseDTO createVehicle(VehicleRequestDTO dto);

    VehicleResponseDTO updateVehicleById(Long id, VehicleRequestDTO dto);

    void deleteVehicleById(Long id);
}
