package com.manosgrigorakis.logisticsplatform.model;

import com.manosgrigorakis.logisticsplatform.enums.QuoteStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotes")
@Getter
@Setter
@ToString(exclude = {"user", "customer", "quoteItems"})
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "validity_days", nullable = false)
    private Integer validityDays = 14;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "tax_rate_percentage", nullable = false)
    private Integer taxRatePercentage;

    @Column(name = "net_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal netPrice;

    @Column(name = "vat_amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal vatAmount;

    @Column(name = "grossPrice", precision = 19, scale = 4, nullable = false)
    private BigDecimal grossPrice;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "special_terms", columnDefinition = "TEXT")
    private String specialTerms;

    @Enumerated(EnumType.STRING)
    @Column(name = "quote_status", nullable = false)
    private QuoteStatus quoteStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteItem> quoteItems = new ArrayList<>();

    public Quote() {
    }

    @Builder
    public Quote(String number, Integer validityDays, String origin, String destination, BigDecimal price, String notes,
                 String specialTerms, QuoteStatus quoteStatus, User user, Customer customer,
                 List<QuoteItem> quoteItems) {
        this.number = number;
        this.validityDays = validityDays;
        this.origin = origin;
        this.destination = destination;
        this.notes = notes;
        this.specialTerms = specialTerms;
        this.quoteStatus = quoteStatus;
        this.user = user;
        this.customer = customer;

        if(quoteItems != null) {
            this.quoteItems = quoteItems;
        }
    }

    @PrePersist()
    public void onPersist() {
        this.createdAt = LocalDateTime.now();

        // Calculate expiration date
        this.issueDate = LocalDate.now();
        this.expirationDate = issueDate.plusDays(validityDays);
    }

    @PreUpdate()
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addQuoteItem(QuoteItem item) {
        quoteItems.add(item);
        item.setQuote(this);
    }

    public void removeQuoteItem(QuoteItem item) {
        quoteItems.remove(item);
        item.setQuote(this);
    }

    /**
     * Returns {@code true} if quote status is finalized
     * otherwise it returns {@code false}
     */
    public boolean isFinalized() {
        return this.quoteStatus.isFinal();
    }

    /**
     * Returns {@code true} if quote is editable
     * otherwise it returns {@code false}
     */
    public boolean isEditable() {
        return this.quoteStatus == QuoteStatus.DRAFT;
    }

    /**
     * Returns {@code true} if quote is expired,
     * otherwise it returns {@code false}
     */
    public boolean isExpired() {
        return this.quoteStatus.isExpired();
    }
}
