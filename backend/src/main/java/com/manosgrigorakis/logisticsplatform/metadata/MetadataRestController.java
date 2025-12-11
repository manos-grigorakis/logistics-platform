package com.manosgrigorakis.logisticsplatform.metadata;

import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteItemUnit;
import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/metadata")
@Tag(name = "Metadata", description = "System metadata")
public class MetadataRestController {

    @Operation(summary = "ENUM Customer Types", description = "List of customer types")
    @ApiResponse(responseCode = "200", description = "List of customer types")
    @GetMapping("/customer-types")
    public List<String> getCustomerTypes() {
        return Arrays.stream(CustomerType.values())
                .map(Enum::name)
                .toList();
    }

    @Operation(summary = "ENUM Quote Status", description = "List of quote status")
    @ApiResponse(responseCode = "200", description = "List of quote status")
    @GetMapping("/quote-statuses")
    public List<String> getQuoteStatus() {
        return Arrays.stream(QuoteStatus.values())
                .map(Enum::name)
                .toList();
    }

    @Operation(summary = "ENUM Quote Item Units", description = "List of quote items units")
    @ApiResponse(responseCode = "200", description = "List of quote items units")
    @GetMapping("/quote-item-units")
    public List<String> getQuoteItemUnits() {
        return Arrays.stream(QuoteItemUnit.values())
                .map(Enum::name)
                .toList();
    }
}
