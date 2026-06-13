package com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;

public record SupplierFilterRequest(
        @Schema(title = "Supplier Company Name", description = "Supplier Company Name", example = "Revoil")
        String companyName
) {}
