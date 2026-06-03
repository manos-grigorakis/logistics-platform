package com.manosgrigorakis.logisticsplatform.metadata;

import com.manosgrigorakis.logisticsplatform.common.dto.ApiResponseWrapper;
import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteItemUnit;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import com.manosgrigorakis.logisticsplatform.shipments.dto.shipment.ShipmentStatusResponse;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentCargoUnit;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/v1/metadata")
@Tag(name = "Metadata", description = "System metadata")
public class MetadataRestController {

    @Operation(summary = "ENUM Customer Types", description = "List of customer types")
    @ApiResponse(responseCode = "200", description = "List of customer types")
    @GetMapping("/customer-types")
    public ApiResponseWrapper<List<String>> getCustomerTypes() {
        return new ApiResponseWrapper<>(Arrays.stream(CustomerType.values()).map(Enum::name).toList());
    }

    @Operation(summary = "ENUM Quote Status", description = "List of quote status")
    @ApiResponse(responseCode = "200", description = "List of quote status")
    @GetMapping("/quote-statuses")
    public ApiResponseWrapper<List<String>> getQuoteStatus() {
        return new ApiResponseWrapper<>(Arrays.stream(QuoteStatus.values()).map(Enum::name).toList());
    }

    @Operation(summary = "ENUM Quote Item Units", description = "List of quote items units")
    @ApiResponse(responseCode = "200", description = "List of quote items units")
    @GetMapping("/quote-item-units")
    public ApiResponseWrapper<List<String>> getQuoteItemUnits() {
        return new ApiResponseWrapper<>(Arrays.stream(QuoteItemUnit.values()).map(Enum::name).toList());
    }

    @Operation(summary = "ENUM Shipment Statuses", description = "List of shipment statuses")
    @ApiResponse(responseCode = "200", description = "List of shipment statuses")
    @GetMapping("/shipment-statuses")
    public ApiResponseWrapper<List<ShipmentStatusResponse>> getShipmentStatuses() {
        return new ApiResponseWrapper<>(Arrays.stream(ShipmentStatus.values()).map(
                status -> new ShipmentStatusResponse(status.getLabel(), status.isEditable(),
                                                     status.isFinalized())).toList());
    }

    @Operation(summary = "ENUM Shipment Cargo Unit", description = "List of shipment cargo units")
    @ApiResponse(responseCode = "200", description = "List of shipment cargo units")
    @GetMapping("/shipment-cargo-units")
    public ApiResponseWrapper<List<String>> getShipmentCargosUnits() {
        return new ApiResponseWrapper<>(Arrays.stream(ShipmentCargoUnit.values()).map(Enum::name).toList());
    }
}
