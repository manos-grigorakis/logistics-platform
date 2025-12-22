package com.manosgrigorakis.logisticsplatform.shipments.dto;

import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.QuoteSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.UserSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.VehicleSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;

import java.time.LocalDateTime;

public record ShipmentResponseDTO(
        Long id,
        ShipmentStatus status,
        String number,
        LocalDateTime pickup,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        QuoteSummaryDTO quote,
        UserSummaryDTO driver,
        UserSummaryDTO createdByUser,
        VehicleSummaryDTO truck,
        VehicleSummaryDTO trailer
)
{ }
