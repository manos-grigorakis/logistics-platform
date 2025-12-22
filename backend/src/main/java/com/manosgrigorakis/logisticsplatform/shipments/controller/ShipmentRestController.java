package com.manosgrigorakis.logisticsplatform.shipments.controller;

import com.manosgrigorakis.logisticsplatform.auth.model.UserInfoDetails;
import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentFilterRequest;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.UpdateShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@Tag(name = "Shipments")
public class ShipmentRestController {
    private final ShipmentService shipmentService;

    public ShipmentRestController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @Operation(summary = "Get All Shipments", description = "Gets the shipments collection with pagination, filtering and sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded shipments"),
            @ApiResponse(responseCode = "400", description = "pickupFrom must be before pickupTo"),
    })
    @GetMapping()
    public Page<ShipmentResponseDTO> getAllShipments(
            @ParameterObject @ModelAttribute @Valid PageFilterRequest pageFilter,
            @ParameterObject @ModelAttribute SortFilterRequest sortFilter,
            @ParameterObject @ModelAttribute @Valid ShipmentFilterRequest shipmentFilter
            )
    {
        return shipmentService.getAllShipments(pageFilter, sortFilter, shipmentFilter);
    }

    @Operation(summary = "Get Shipment by Id", description = "Find Shipment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Founded shipment"),
            @ApiResponse(responseCode = "404", description = "Shipment id doesn't exist"),
    })
    @GetMapping("/{id}")
    public ShipmentResponseDTO getShipmentById(@PathVariable Long id) {
        return shipmentService.getShipmentById(id);
    }

    @Operation(summary = "Create a Shipment", description = "Creates a new shipment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Shipment created"),
            @ApiResponse(responseCode = "400", description = "Validation Error"),
            @ApiResponse(responseCode = "404", description = """
                            Resource not found. Possible causes:\s
                            - Quote Id doesn't exist\s
                            - Created by User Id` doesn't exist\s
                            - Driver Id doesn't exist (If provided)\s
                            - Truck Id doesn't exist (If provided)\s
                            - Trailer Id doesn't exist (If provided)\s
                            """
            ),
            @ApiResponse(responseCode = "409", description = """
                    Conflict. Possible causes:
                    - Quote has no accepted status\s
                    - Shipment for that quote already exist\s
                    - Driver doesn't have DRIVER role assigned\s
                    """
            ),
    })
    @PostMapping
    public ResponseEntity<ShipmentResponseDTO> createShipment(@RequestBody @Valid ShipmentRequestDTO dto) {
        ShipmentResponseDTO response = shipmentService.createShipment(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a Shipment", description = "Updates a shipment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shipment updated"),
            @ApiResponse(responseCode = "400", description = "Validation Error"),
            @ApiResponse(responseCode = "404", description = "Shipment doesn't exists"),
            @ApiResponse(responseCode = "409", description = "Shipment is finalized and cannot be updated"),
    })
    @PutMapping("/{id}")
    public ShipmentResponseDTO updateShipmentById(@PathVariable Long id, @RequestBody @Valid UpdateShipmentRequestDTO dto) {
        return shipmentService.updateShipmentById(id, dto);
    }

    @Operation(summary = "Get Shipments by Driver", description = "Gets all shipments assigned specifically to the authenticated driver")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shipments that driver has"),
            @ApiResponse(responseCode = "400", description = "Validation Error"),
    })
    @GetMapping("/driver")
    public Page<ShipmentResponseDTO> getShipmentsByDriver(
            @AuthenticationPrincipal UserInfoDetails userInfoDetails,
            @ParameterObject @ModelAttribute @Valid PageFilterRequest pageFilter,
            @ParameterObject @ModelAttribute SortFilterRequest sortFilter
    )
    {
        // Get driver's id from JWT
        Long driverId = userInfoDetails.getUserId();

        return shipmentService.getShipmentsByDriver(driverId, pageFilter, sortFilter);
    }
}
