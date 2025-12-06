package com.manosgrigorakis.logisticsplatform.quotes.model;

import com.manosgrigorakis.logisticsplatform.quotes.enums.QuoteItemUnit;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quote_items")
@Getter
@Setter
@ToString(exclude = {"quote"})
public class QuoteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit", nullable = false)
    @Enumerated(EnumType.STRING)
    private QuoteItemUnit unit;

    @Column(name = "price", precision = 19, scale = 4, nullable = false)
    private BigDecimal price;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    public QuoteItem() {
    }

    @Builder
    public QuoteItem(
            String name,
            String description,
            Integer quantity,
            QuoteItemUnit unit,
            BigDecimal price,
            Quote quote)
    {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.quote = quote;
    }

    @PrePersist()
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate()
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
