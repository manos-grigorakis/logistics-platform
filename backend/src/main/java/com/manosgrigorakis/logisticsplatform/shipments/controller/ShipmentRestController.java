package com.manosgrigorakis.logisticsplatform.shipments.controller;

import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentFilterRequest;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.UpdateShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.service.ShipmentService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
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

    @GetMapping()
    public Page<ShipmentResponseDTO> getAllShipments(
            @ParameterObject @ModelAttribute @Valid PageFilterRequest pageFilter,
            @ParameterObject @ModelAttribute SortFilterRequest sortFilter,
            @ParameterObject @ModelAttribute @Valid ShipmentFilterRequest shipmentFilter
            )
    {
        return shipmentService.getAllShipments(pageFilter, sortFilter, shipmentFilter);
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

    @PutMapping("/{id}")
    public ShipmentResponseDTO updateShipmentById(@PathVariable Long id, @RequestBody @Valid UpdateShipmentRequestDTO dto) {
        return shipmentService.updateShipmentById(id, dto);
    }
}
