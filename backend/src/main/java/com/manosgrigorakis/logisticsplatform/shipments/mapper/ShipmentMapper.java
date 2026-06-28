package com.manosgrigorakis.logisticsplatform.shipments.mapper;

import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.ShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.ShipmentResponseDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.UpdateShipmentRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipmentCargo.ShipmentCargoRequestDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.ShipmentStatusSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.dto.summary.UserSummaryDTO;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import com.manosgrigorakis.logisticsplatform.shipments.model.ShipmentCargo;
import com.manosgrigorakis.logisticsplatform.shipments.model.Vehicle;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = ShipmentCargoMapper.class)
public interface ShipmentMapper {
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "notes", source = "dto.notes")
    Shipment toEntity(ShipmentRequestDTO dto, Quote quote, User driver, User createdByUser, Vehicle truck,
                      Vehicle trailer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "quote", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "shipmentCargos", ignore = true)
    @Mapping(target = "notes", source = "dto.notes")
    void toUpdate(@MappingTarget Shipment shipment, UpdateShipmentRequestDTO dto, User driver, Vehicle truck,
                  Vehicle trailer);

    @Mapping(target = "status", source = "status", qualifiedByName = "toStatusSummary")
    @Mapping(target = "cargoItems", source = "shipmentCargos")
    ShipmentResponseDTO toResponse(Shipment shipment);

    ShipmentCargo toShipmentCargo(ShipmentCargoRequestDTO dto);

    @AfterMapping
    default void linkCargoItemsBackToShipment(@MappingTarget Shipment shipment, ShipmentRequestDTO dto) {
        if (dto == null || dto.getCargoItems() == null) return;

        dto.getCargoItems().forEach(item -> shipment.addShipmentCargoItem(toShipmentCargo(item)));
    }

    @Named("toStatusSummary")
    default ShipmentStatusSummaryDTO toStatusSummary(ShipmentStatus status) {
        if(status == null) return null;
        return new ShipmentStatusSummaryDTO(status.getLabel(), status.isEditable(), status.isFinalized());
    }

    @Mapping(target = "fullName", expression = "java(user.fullName())")
    UserSummaryDTO toUserSummary(User user);
}
