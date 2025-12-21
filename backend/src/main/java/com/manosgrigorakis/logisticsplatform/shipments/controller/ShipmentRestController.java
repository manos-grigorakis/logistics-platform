package com.manosgrigorakis.logisticsplatform.shipments.controller;

import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentRestController {
    private final ShipmentService shipmentService;

    public ShipmentRestController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping("/{id}")
    public ShipmentResponseDTO getShipmentById(@PathVariable Long id) {
        return shipmentService.getShipmentById(id);
    }

    @PostMapping
    public ResponseEntity<ShipmentResponseDTO> createShipment(@RequestBody @Valid ShipmentRequestDTO dto) {
        ShipmentResponseDTO response = shipmentService.createShipment(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
