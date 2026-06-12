package com.manosgrigorakis.logisticsplatform.suppliers.mapper;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.model.Supplier;

public class SupplierMapper {
    // Request -> Entity
    public static Supplier toEntity(SupplierRequest request) {
        return Supplier.builder()
                .companyName(request.companyName())
                .email(request.email())
                .build();
    }

    // Entity -> Response
    public static SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(supplier.getId(), supplier.getCompanyName(), supplier.getEmail(),
                                    supplier.getCreatedAt(), supplier.getUpdatedAt());
    }
}
