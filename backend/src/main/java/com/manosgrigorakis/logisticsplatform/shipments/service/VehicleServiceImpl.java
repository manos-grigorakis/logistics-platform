package com.manosgrigorakis.logisticsplatform.shipments.service;

import com.manosgrigorakis.logisticsplatform.audit.dto.AuditEventDTO;
import com.manosgrigorakis.logisticsplatform.audit.enums.AuditAction;
import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.common.utils.EntityChangeTracker;
import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.mapper.VehicleMapper;
import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;
import com.manosgrigorakis.logisticsplatform.shipments.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class VehicleServiceImpl implements VehicleService{
    private final VehicleRepository vehicleRepository;
    private final AuditService auditService;
    private final VehicleMapper vehicleMapper;


    @Cacheable(value = "vehicles", key = "'all-vehicles'")
    @Override
    public List<VehicleResponseDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream().map(vehicleMapper::toResponse).toList();
    }

    @Cacheable(value = "vehicles", key = "#id")
    @Override
    public VehicleResponseDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> {
            log.warn("Vehicle not found with id {}", id);
            return new ResourceNotFoundException("Vehicle not found with id: " + id);
        });

        return vehicleMapper.toResponse(vehicle);
    }

    @CacheEvict(value = "vehicles", allEntries = true)
    @Override
    public VehicleResponseDTO createVehicle(VehicleRequestDTO dto) {
        if(vehicleRepository.existsVehicleByPlate(dto.getPlate())) {
            log.warn("Vehicle already exists with plate {}", dto.getPlate());
            throw new DuplicateEntryException("plate", dto.getPlate());
        }

        Vehicle vehicle = vehicleMapper.toEntity(dto);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created with plate {}", dto.getPlate());
        this.logVehicle(vehicle, AuditAction.CREATE);

        return vehicleMapper.toResponse(savedVehicle);
    }

    @Caching(evict = {
            @CacheEvict(value = "vehicles", key = "'all-vehicles'"),
            @CacheEvict(value = "vehicles", key = "#id"),
            @CacheEvict(value = "shipments", allEntries = true)
    })
    @Override
    public VehicleResponseDTO updateVehicleById(Long id, VehicleRequestDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> {
                    log.warn("Vehicle not found with id {}", id);
                    return new ResourceNotFoundException("Vehicle not found with id: " + id);
                });

        Vehicle oldVehicle = new Vehicle(vehicle);

        if(vehicleRepository.existsByPlateAndIdNot(dto.getPlate(), id)) {
            log.warn("Vehicle already exists with plate {}", dto.getPlate());
            throw new DuplicateEntryException("plate", dto.getPlate());
        }

        vehicleMapper.toUpdate(vehicle, dto);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated with plate {}", updatedVehicle.getPlate());
        this.logUpdatedVehicle(oldVehicle, updatedVehicle);
        return vehicleMapper.toResponse(updatedVehicle);
    }

    @Caching(evict = {
            @CacheEvict(value = "vehicles", key = "'all-vehicles'"),
            @CacheEvict(value = "vehicles", key = "#id")
    })
    @Override
    public void deleteVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Delete failed. Vehicle not found with id: {}", id);
                    return new ResourceNotFoundException("Vehicle not found with id: " + id);
                });

        vehicleRepository.deleteById(id);
        log.info("Vehicle deleted: {}", id);
        this.logVehicle(vehicle, AuditAction.DELETE);
    }

    /**
     * Logs vehicle operation in the audit system
     * @param vehicle The vehicle
     * @param action The action take {@link AuditAction}
     */
    private void logVehicle(Vehicle vehicle, AuditAction action) {
        this.auditService.log(
                AuditEventDTO.builder()
                        .entityType("Vehicle")
                        .entityId(vehicle.getId())
                        .notes("Brand: " + vehicle.getBrand() + " | Plate: " + vehicle.getPlate())
                        .action(action)
                        .build()

        );
    }

    /**
     * Logs the updated vehicle with the changed values only in the audit system
     * @param oldVehicle Old values of vehicle before updating
     * @param updatedVehicle Updated values of vehicle
     */
    private void logUpdatedVehicle(Vehicle oldVehicle, Vehicle updatedVehicle) {
        Map<String, Object> changes = new HashMap<>();

        EntityChangeTracker.trackFieldChange(changes, "brand", Vehicle::getBrand, oldVehicle, updatedVehicle);
        EntityChangeTracker.trackFieldChange(changes, "plate", Vehicle::getPlate, oldVehicle, updatedVehicle);
        EntityChangeTracker.trackFieldChange(changes, "type", Vehicle::getType, oldVehicle, updatedVehicle);

        if(changes.isEmpty()) return;

        this.auditService.log(
                AuditEventDTO.builder()
                        .entityType("Vehicle")
                        .entityId(oldVehicle.getId())
                        .changes(changes)
                        .action(AuditAction.UPDATE)
                        .build()

        );
    }
}
