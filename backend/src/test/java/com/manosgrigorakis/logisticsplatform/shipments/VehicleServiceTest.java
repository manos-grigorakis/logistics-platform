package com.manosgrigorakis.logisticsplatform.shipments;

import com.manosgrigorakis.logisticsplatform.audit.service.AuditService;
import com.manosgrigorakis.logisticsplatform.common.exception.DuplicateEntryException;
import com.manosgrigorakis.logisticsplatform.common.exception.ResourceNotFoundException;
import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.enums.VehicleType;
import com.manosgrigorakis.logisticsplatform.shipments.mapper.VehicleMapper;
import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;
import com.manosgrigorakis.logisticsplatform.shipments.repository.VehicleRepository;
import com.manosgrigorakis.logisticsplatform.shipments.service.VehicleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {
    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private static final String VEHICLE_PLATE = "AAA-1234";
    private static final VehicleResponseDTO MOCK_VEHICLE_RESPONSE = new VehicleResponseDTO(1L, null, "AAA-1234",
                                                                                           VehicleType.TRUCK, null,
                                                                                           null);

    @Test
    void getAllVehicles_shouldReturnAllVehicles() {
        // Arrange
        List<Vehicle> mockVehicles = List.of(buildVehicle(), buildVehicle());
        when(vehicleRepository.findAll()).thenReturn(mockVehicles);
        when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(MOCK_VEHICLE_RESPONSE);

        // Act
        List<VehicleResponseDTO> response = vehicleService.getAllVehicles();

        // Assert
        assertNotNull(response);
        assertEquals(mockVehicles.size(), response.size());
        verify(vehicleRepository, times(1)).findAll();
        verify(vehicleMapper, times(2)).toResponse(any(Vehicle.class));
    }

    @Test
    void getVehicleById_shouldReturnVehicle() {
        // Arrange
        Vehicle vehicle = buildVehicle();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toResponse(vehicle)).thenReturn(MOCK_VEHICLE_RESPONSE);

        // Act
        VehicleResponseDTO responseDTO = vehicleService.getVehicleById(1L);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(vehicle.getPlate(), responseDTO.plate());
        assertEquals(vehicle.getType(), responseDTO.type());
        verify(vehicleRepository, times(1)).findById(1L);
    }

    @Test
    void getVehicleById_shouldThrowNotFoundException_whenVehicleNotExists() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> vehicleService.getVehicleById(1L));
        verify(vehicleRepository, times(1)).findById(1L);
    }

    @Test
    void createVehicle_shouldCreateVehicle() {
        // Arrange
        Vehicle mockVehicle = buildVehicle();
        VehicleRequestDTO request = buildRequest();

        when(vehicleRepository.existsVehicleByPlate(request.getPlate())).thenReturn(false);
        when(vehicleMapper.toEntity(request)).thenReturn(mockVehicle);
        when(vehicleRepository.save(mockVehicle)).thenReturn(mockVehicle);
        when(vehicleMapper.toResponse(mockVehicle)).thenReturn(MOCK_VEHICLE_RESPONSE);

        // Act
        VehicleResponseDTO response = vehicleService.createVehicle(request);

        // Assert
        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository, times(1)).save(captor.capture());

        Vehicle savedVehicle = captor.getValue();

        assertNotNull(response);
        assertEquals(request.getPlate(), savedVehicle.getPlate());
        assertEquals(request.getType(), savedVehicle.getType());
        verify(vehicleRepository, times(1)).existsVehicleByPlate(VEHICLE_PLATE);
    }

    @Test
    void createVehicle_shouldThrowDuplicateException_whenVehicleAlreadyExistsByPlate() {
        // Arrange
        VehicleRequestDTO request = buildRequest();

        when(vehicleRepository.existsVehicleByPlate(VEHICLE_PLATE)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> vehicleService.createVehicle(request));
        verify(vehicleRepository, times(1)).existsVehicleByPlate(VEHICLE_PLATE);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void updateVehicle_shouldUpdateVehicle() {
        // Arrange
        Vehicle mockVehicle = buildVehicle();
        VehicleRequestDTO request = buildRequest();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(mockVehicle));
        when(vehicleRepository.existsByPlateAndIdNot(VEHICLE_PLATE, 1L)).thenReturn(false);
        doNothing().when(vehicleMapper).toUpdate(mockVehicle, request);
        when(vehicleMapper.toResponse(mockVehicle)).thenReturn(MOCK_VEHICLE_RESPONSE);
        when(vehicleRepository.save(mockVehicle)).thenReturn(mockVehicle);

        // Act
        VehicleResponseDTO response = vehicleService.updateVehicleById(1L, request);

        // Assert
        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository, times(1)).save(captor.capture());

        Vehicle savedVehicle = captor.getValue();
        assertNotNull(response);
        assertEquals(mockVehicle.getPlate(), savedVehicle.getPlate());
        assertEquals(mockVehicle.getType(), savedVehicle.getType());

        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).existsByPlateAndIdNot(request.getPlate(), 1L);
    }

    @Test
    void updateVehicle_shouldThrowNotFoundException_whenVehicleNotExists() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> vehicleService.updateVehicleById(1L, new VehicleRequestDTO()));
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, never()).existsVehicleByPlate(anyString());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void updateVehicle_shouldThrowDuplicateEntryException_whenVehicleExistsByPlate() {
        // Arrange
        VehicleRequestDTO request = buildRequest();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(buildVehicle()));
        when(vehicleRepository.existsByPlateAndIdNot(VEHICLE_PLATE, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEntryException.class, () -> vehicleService.updateVehicleById(1L, request));
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).existsByPlateAndIdNot(request.getPlate(), 1L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void deleteVehicle_shouldDeleteVehicle() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(buildVehicle()));

        // Act
        vehicleService.deleteVehicleById(1L);

        // Assert
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).deleteById(1L);

    }

    @Test
    void deleteVehicle_shouldThrowNotFoundException_whenVehicleNotExists() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> vehicleService.deleteVehicleById(1L));
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, never()).deleteById(1L);
    }

    /**
     * Builds a vehicle for testing purposes
     * @return The created vehicle
     */
    private Vehicle buildVehicle() {
        return Vehicle.builder().plate(VEHICLE_PLATE).type(VehicleType.TRUCK).build();
    }

    /**
     * Build a Vehicle request for testing purposes
     * @return The created request
     */
    private VehicleRequestDTO buildRequest() {
        VehicleRequestDTO request = new VehicleRequestDTO();
        request.setPlate(VEHICLE_PLATE);
        request.setType(VehicleType.TRUCK);
        return request;
    }
}
