package com.manosgrigorakis.logisticsplatform.suppliers.mapper;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierPaymentResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.SupplierSummaryResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.model.Supplier;
import com.manosgrigorakis.logisticsplatform.suppliers.model.SupplierPayment;

import java.math.BigDecimal;

public class SupplierPaymentMapper {
    // Request -> Entity
    public static SupplierPayment toEntity(SupplierPaymentRequest request, String number, Supplier supplier) {
        return SupplierPayment.builder()
                .number(number)
                .title(request.title())
                .description(request.description())
                .totalAmount(request.totalAmount())
                .paidAmount(request.paidAmount() != null ? request.paidAmount() : BigDecimal.ZERO)
                .type(request.type())
                .supplier(supplier)
                .build();
    }

    // Entity -> Response
    public static SupplierPaymentResponse toResponse(SupplierPayment payment) {
        Supplier supplier = payment.getSupplier();
        SupplierSummaryResponse supplierSummary = new SupplierSummaryResponse(supplier.getId(),
                                                                              supplier.getCompanyName());

        return new SupplierPaymentResponse(payment.getId(), payment.getNumber(), payment.getTitle(),
                                           payment.getDescription(), payment.getTotalAmount(), payment.getPaidAmount(),
                                           payment.getUnpaidAmount(), payment.getStatus(), payment.getType(),
                                           payment.getInvoiceUrl(), payment.getReceiptUrl(), supplierSummary,
                                           payment.getCreatedAt(),
                                           payment.getUpdatedAt());
    }
}
