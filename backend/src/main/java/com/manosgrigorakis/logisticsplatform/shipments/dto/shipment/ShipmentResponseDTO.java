package com.manosgrigorakis.logisticsplatform.shipments.dto.shipment;

import com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo.ShipmentCargoResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.QuoteSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.ShipmentStatusSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.UserSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.VehicleSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public record ShipmentResponseDTO(
        Long id,
        ShipmentStatusSummaryDTO status,
        String number,
        LocalDateTime pickup,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        QuoteSummaryDTO quote,
        UserSummaryDTO driver,
        UserSummaryDTO createdByUser,
        VehicleSummaryDTO truck,
        VehicleSummaryDTO trailer,
        List<ShipmentCargoResponseDTO> cargoItems
)
{ }
