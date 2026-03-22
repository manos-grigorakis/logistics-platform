package com.manosgrigorakis.logisticsplatform.payments.model;

import com.manosgrigorakis.logisticsplatform.customers.model.Customer;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconciliation_reports")
@Getter
@Setter
@ToString(exclude = {"customer"})
public class ReconciliationReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "matched_invoices", nullable = false)
    private Integer matchedInvoices;

    @Column(name = "unmatched_invoices", nullable = false)
    private Integer unmatchedInvoices;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public ReconciliationReport() {}

    @Builder
    public ReconciliationReport(String name, String fileUrl, LocalDate fromDate, LocalDate toDate,
                                Integer matchedInvoices, Integer unmatchedInvoices, Customer customer) {
        this.name = name;
        this.fileUrl = fileUrl;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.matchedInvoices = matchedInvoices;
        this.unmatchedInvoices = unmatchedInvoices;
        this.customer = customer;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
