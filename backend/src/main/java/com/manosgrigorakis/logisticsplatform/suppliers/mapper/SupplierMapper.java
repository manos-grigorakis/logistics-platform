package com.manosgrigorakis.logisticsplatform.suppliers.mapper;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplier.SupplierResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toEntity(SupplierRequest request);

    void toUpdate(@MappingTarget Supplier supplier, SupplierRequest request);

    @Mapping(target = "isActive", source = "active")
    SupplierResponse toResponse(Supplier supplier);
}
