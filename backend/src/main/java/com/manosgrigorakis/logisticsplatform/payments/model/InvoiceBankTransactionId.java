package com.manosgrigorakis.logisticsplatform.payments.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Data
public class InvoiceBankTransactionId implements Serializable {
    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "bank_transaction_id")
    private Long bankTransactionId;
}
