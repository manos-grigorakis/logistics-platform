package com.manosgrigorakis.logisticsplatform.payments.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bank_transactions")
@Getter
@Setter
@ToString(exclude = {"invoicePayments"})
public class BankTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "bank_name", nullable = false, length = 80)
    private String bankName;

    @Column(name = "sender_name", length = 100)
    private String senderName;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "bankTransaction")
    private List<InvoicePayments> invoicePayments;

    public BankTransaction() {}

    @Builder
    public BankTransaction(
            String bankName,
            String senderName,
            BigDecimal amount,
            String description,
            LocalDate issueDate
    ) {
        this.bankName = bankName;
        this.senderName = senderName;
        this.amount = amount;
        this.description = description;
        this.issueDate = issueDate;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
