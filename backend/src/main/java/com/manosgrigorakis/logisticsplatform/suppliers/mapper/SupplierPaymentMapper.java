package com.manosgrigorakis.logisticsplatform.suppliers.mapper;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentCreateRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentUpdateRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.model.Supplier;
import com.manosgrigorakis.logisticsplatform.suppliers.model.SupplierPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface SupplierPaymentMapper {

    @Mapping(target = "number", source = "number")
    @Mapping(target = "supplier", source = "supplier")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "paidAmount", source = "request.paidAmount", qualifiedByName = "normalizePaidAmount")
    SupplierPayment toEntity(SupplierPaymentCreateRequest request, String number, Supplier supplier);

    @Mapping(target = "paidAmount", source = "request.paidAmount", qualifiedByName = "normalizePaidAmount")
    void toUpdateEntity(@MappingTarget SupplierPayment payment, SupplierPaymentUpdateRequest request);

    @Mapping(target = "invoiceUrl", source = "invoicePresignedUrl")
    @Mapping(target = "receiptUrl", source = "receiptPresignedUrl")
    SupplierPaymentResponse toResponse(SupplierPayment payment, String invoicePresignedUrl, String receiptPresignedUrl);

    @Named("normalizePaidAmount")
    default BigDecimal normalizePaidAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }
}
