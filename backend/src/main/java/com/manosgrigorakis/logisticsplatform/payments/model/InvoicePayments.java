package com.manosgrigorakis.logisticsplatform.payments.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoice_payments")
@Getter
@Setter
@NoArgsConstructor
public class InvoicePayments {
    @EmbeddedId
    private InvoiceBankTransactionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("invoiceId")
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bankTransactionId")
    @JoinColumn(name = "bank_transaction_id")
    private BankTransaction bankTransaction;
}
