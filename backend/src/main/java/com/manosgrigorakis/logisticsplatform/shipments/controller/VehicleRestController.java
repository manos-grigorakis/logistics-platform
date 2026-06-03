package com.manosgrigorakis.logisticsplatform.shipments.controller;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.vehicle.VehicleResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/v1/vehicles")
@Tag(name = "Vehicles", description = "CRUD operation for vehicles")
public class VehicleRestController {
    private final VehicleService vehicleService;

    public VehicleRestController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Get All Vehicles", description = "Lists all the vehicles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List all the vehicles"),
    })
    @GetMapping()
    public ApiResponseWrapper<List<VehicleResponseDTO>> getAllVehicles() {
        return new ApiResponseWrapper<>(vehicleService.getAllVehicles());
    }

    @Operation(summary = "Get Vehicle by Id", description = "Find vehicle by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded vehicle"),
            @ApiResponse(responseCode = "404", description = "Vehicle doesn't exist"),
    })
    @GetMapping("/{id}")
    public ApiResponseWrapper<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        return new ApiResponseWrapper<>(vehicleService.getVehicleById(id));
    }

    @Operation(summary = "Create a Vehicle", description = "Create a new vehicle")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
            @ApiResponse(responseCode = "409", description = "Vehicle with plate already exists")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ApiResponseWrapper<VehicleResponseDTO> createVehicle(@RequestBody @Valid VehicleRequestDTO dto) {
        return new ApiResponseWrapper<>(vehicleService.createVehicle(dto));
    }

    @Operation(summary = "Update a Vehicle by Id", description = "Update a vehicle by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Vehicle with plate already exists")
    })
    @PutMapping("/{id}")
    public ApiResponseWrapper<VehicleResponseDTO> updateVehicleById(@PathVariable Long id,
                                                                    @RequestBody @Valid VehicleRequestDTO dto) {
        return new ApiResponseWrapper<>(vehicleService.updateVehicleById(id, dto));
    }

    @Operation(summary = "Delete a Vehicle by Id", description = "Delete a vehicle by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle doesn't exist")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteVehicleById(@PathVariable Long id) {
        vehicleService.deleteVehicleById(id);
    }
}
