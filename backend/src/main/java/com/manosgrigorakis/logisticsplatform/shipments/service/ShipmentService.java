package com.manosgrigorakis.logisticsplatform.shipments.service;

import com.manosgrigorakis.logisticsplatform.common.dto.PageFilterRequest;
import com.manosgrigorakis.logisticsplatform.common.dto.SortFilterRequest;
import com.manosgrigorakis.logisticsplatform.shipments.dto.ShipmentFilterRequest;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.ShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.ShipmentResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.UpdateShipmentRequestDTO;
import org.springframework.data.domain.Page;

public interface ShipmentService {
    Page<ShipmentResponseDTO> getAllShipments(PageFilterRequest page, SortFilterRequest sort, ShipmentFilterRequest filterRequest);

    ShipmentResponseDTO getShipmentById(Long id);

    ShipmentResponseDTO createShipment(ShipmentRequestDTO dto);

    ShipmentResponseDTO updateShipmentById(Long id, UpdateShipmentRequestDTO dto);

    Page<ShipmentResponseDTO> getShipmentsByDriver(Long driverId, PageFilterRequest pageFilter, SortFilterRequest sortFilter);
}
