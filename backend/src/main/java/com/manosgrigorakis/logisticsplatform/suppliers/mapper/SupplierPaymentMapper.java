package com.manosgrigorakis.logisticsplatform.suppliers.mapper;

import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentCreateRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierPaymentUpdateRequest;
import com.manosgrigorakis.logisticsplatform.suppliers.dto.supplierpayment.SupplierSummaryResponse;
import com.manosgrigorakis.logisticsplatform.suppliers.model.Supplier;
import com.manosgrigorakis.logisticsplatform.suppliers.model.SupplierPayment;

import java.math.BigDecimal;

public class SupplierPaymentMapper {
    // Create Request -> Entity
    public static SupplierPayment toEntity(SupplierPaymentCreateRequest request, String number, Supplier supplier) {
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

    // Update Request -> Entity
    public static SupplierPayment toUpdateEntity(SupplierPayment payment, SupplierPaymentUpdateRequest request) {
        payment.setTitle(request.title());
        payment.setDescription(request.description());
        payment.setTotalAmount(request.totalAmount());
        payment.setPaidAmount(request.paidAmount());
        payment.setType(request.type());
        return payment;
    }

    // Entity -> Response
    public static SupplierPaymentResponse toResponse(SupplierPayment payment, String invoicePresignedUrl,
                                                     String receiptPresignedUrl) {
        Supplier supplier = payment.getSupplier();
        SupplierSummaryResponse supplierSummary = new SupplierSummaryResponse(supplier.getId(),
                                                                              supplier.getCompanyName());

        return new SupplierPaymentResponse(payment.getId(), payment.getNumber(), payment.getTitle(),
                                           payment.getDescription(), payment.getTotalAmount(), payment.getPaidAmount(),
                                           payment.getUnpaidAmount(), payment.getStatus(), payment.getType(),
                                           invoicePresignedUrl, receiptPresignedUrl, supplierSummary,
                                           payment.getCreatedAt(),
                                           payment.getUpdatedAt());
    }
}
