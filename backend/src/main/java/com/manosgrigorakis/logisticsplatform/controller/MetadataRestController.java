package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
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
}
