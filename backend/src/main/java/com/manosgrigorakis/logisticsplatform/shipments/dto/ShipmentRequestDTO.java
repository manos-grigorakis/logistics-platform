package com.manosgrigorakis.logisticsplatform.shipments.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentRequestDTO {
    private Long quoteId;
    private Long driverId;
    private Long createdByUserId;
    private Long truckId;
    private Long trailerId;
    private LocalDateTime pickup;
    private String notes;
}
