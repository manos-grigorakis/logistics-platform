package com.manosgrigorakis.logisticsplatform.shipments.mapper;

import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.ShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.ShipmentResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo.ShipmentCargoResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.QuoteSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.UserSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.VehicleSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import com.manosgrigorakis.logisticsplatform.shipments.model.ShipmentCargo;
import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;
import com.manosgrigorakis.logisticsplatform.users.model.User;

import java.util.List;

public class ShipmentMapper {
    // DTO -> Entity
    public static Shipment toEntity(
            ShipmentRequestDTO dto,
            Quote quote,
            User driver,
            User createdByUser,
            Vehicle truck,
            Vehicle trailer
    )
    {
        Shipment shipment = Shipment.builder()
                .pickup(dto.getPickup())
                .notes(dto.getNotes())
                .quote(quote)
                .driver(driver)
                .createdByUser(createdByUser)
                .truck(truck)
                .trailer(trailer)
                .build();

        dto.getCargoItems().forEach(item ->
                shipment.addShipmentCargoItem(ShipmentCargoMapper.toEntity(item))
        );

        return  shipment;
    }

    // Entity -> Response
    public static ShipmentResponseDTO toResponse(Shipment shipment) {
        Quote quote = shipment.getQuote();
        User driver = shipment.getDriver();
        User createdByUser = shipment.getCreatedByUser();
        Vehicle truck = shipment.getTruck();
        Vehicle trailer = shipment.getTrailer();
        List<ShipmentCargo> shipmentCargo = shipment.getShipmentCargos();


        // Summaries
        QuoteSummaryDTO quoteSummary = new QuoteSummaryDTO(quote.getId(), quote.getNumber());
        UserSummaryDTO createsByUserSummary = new UserSummaryDTO(createdByUser.getId(), createdByUser.fullName());
        List<ShipmentCargoResponseDTO> cargoItems = shipmentCargo.stream()
                .map(ShipmentCargoMapper::toResponse)
                .toList();

        return new ShipmentResponseDTO(
                shipment.getId(),
                shipment.getStatus(),
                shipment.getNumber(),
                shipment.getPickup(),
                shipment.getNotes(),
                shipment.getCreatedAt(),
                shipment.getUpdatedAt(),
                quoteSummary,
                toUserSummary(driver),
                createsByUserSummary,
                toVehicleSummary(truck),
                toVehicleSummary(trailer),
                cargoItems
        );
    }

    private static UserSummaryDTO toUserSummary(User user) {
        if (user == null) return null;
        return new UserSummaryDTO(user.getId(), user.fullName());
    }

    private static VehicleSummaryDTO toVehicleSummary(Vehicle vehicle) {
        if(vehicle == null) return null;
        return new VehicleSummaryDTO(vehicle.getId(), vehicle.getPlate(), vehicle.getType());
    }
}
