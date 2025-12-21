package com.manosgrigorakis.logisticsplatform.shipments.service;

import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.shipments.dto.VehicleRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.VehicleResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.mapper.VehicleMapper;
import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;
import com.manosgrigorakis.logisticsplatform.shipments.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService{
    private final VehicleRepository vehicleRepository;

    private final Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<VehicleResponseDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        return vehicles.stream().map(VehicleMapper::toResponse).toList();
    }

    @Override
    public VehicleResponseDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Vehicle not found with id {}", id);
                    return new ResourceNotFoundException("Vehicle not found with id: " + id);
                });

        return VehicleMapper.toResponse(vehicle);
    }

    @Override
    public VehicleResponseDTO createVehicle(VehicleRequestDTO dto) {
        if(vehicleRepository.existsVehicleByPlate(dto.getPlate())) {
            log.warn("Vehicle already exists with plate {}", dto.getPlate());
            throw new DuplicateEntryException("plate", dto.getPlate());
        }

        Vehicle vehicle = Vehicle.builder()
                .brand(dto.getBrand())
                .plate(dto.getPlate())
                .type(dto.getType())
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created with plate {}", dto.getPlate());

        return VehicleMapper.toResponse(savedVehicle);
    }

    @Override
    public VehicleResponseDTO updateVehicleById(Long id, VehicleRequestDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Vehicle not found with id {}", id);
                    return new ResourceNotFoundException("Vehicle not found with id: " + id);
                });

        if(vehicleRepository.existsByPlateAndIdNot(dto.getPlate(), id)) {
            log.warn("Vehicle already exists with plate {}", dto.getPlate());
            throw new DuplicateEntryException("plate", dto.getPlate());
        }

        vehicle.setBrand(dto.getBrand());
        vehicle.setPlate(dto.getPlate());
        vehicle.setType(dto.getType());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated with plate {}", updatedVehicle.getPlate());

        return VehicleMapper.toResponse(updatedVehicle);
    }

    @Override
    public void deleteVehicleById(Long id) {
        if(!vehicleRepository.existsById(id)) {
            log.error("Delete failed. Vehicle not found with id: {}", id);
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }

        vehicleRepository.deleteById(id);
        log.info("Vehicle deleted: {}", id);
    }
}
