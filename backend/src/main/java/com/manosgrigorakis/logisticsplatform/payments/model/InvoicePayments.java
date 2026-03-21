package com.manosgrigorakis.logisticsplatform.payments.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_payments")
@Getter
@Setter
@NoArgsConstructor
public class InvoicePayments {
    @EmbeddedId
    private InvoiceBankTransactionId id = new InvoiceBankTransactionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("invoiceId")
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bankTransactionId")
    @JoinColumn(name = "bank_transaction_id")
    private BankTransaction bankTransaction;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    public InvoicePayments(Invoice invoice, BankTransaction bankTransaction, BigDecimal amount) {
        this.invoice = invoice;
        this.bankTransaction = bankTransaction;
        this.amount = amount;
    }
}
