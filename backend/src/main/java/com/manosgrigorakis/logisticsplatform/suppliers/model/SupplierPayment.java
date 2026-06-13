package com.manosgrigorakis.logisticsplatform.suppliers.model;

import com.manosgrigorakis.logisticsplatform.suppliers.model.enums.SupplierPaymentStatus;
import com.manosgrigorakis.logisticsplatform.suppliers.model.enums.SupplierPaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "supplier_payments")
@Entity
public class SupplierPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "number", unique = true, nullable = false, length = 14)
    private String number;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SupplierPaymentStatus status = SupplierPaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SupplierPaymentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Builder
    public SupplierPayment(String number, String title, String description, BigDecimal totalAmount,
                           BigDecimal paidAmount,
                           SupplierPaymentStatus status, SupplierPaymentType type, Supplier supplier) {
        this.number = number;
        this.title = title;
        this.description = description;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.status = status;
        this.type = type;
        this.supplier = supplier;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getUnpaidAmount() {
        return totalAmount.subtract(paidAmount);
    }

    /**
     * Sets the {@link #status} based on the payment amounts
     *
     * <p>Rules:</p>
     * <ul>
     *     <li>{@link #totalAmount} equals {@link #paidAmount} -> {@link SupplierPaymentStatus#PAID}</li>
     *     <li>{@link #paidAmount} is not {@code ZERO} -> {@link SupplierPaymentStatus#PARTIALLY_PAID}</li>
     *     <li>default -> {@link SupplierPaymentStatus#PENDING}</li>
     * </ul>
     */
    public void setStatusBasedOnAmounts() {
        if (totalAmount.equals(paidAmount)) {
            setStatus(SupplierPaymentStatus.PAID);
        } else if (!paidAmount.equals(BigDecimal.ZERO)) {
            setStatus(SupplierPaymentStatus.PARTIALLY_PAID);
        } else {
            setStatus(SupplierPaymentStatus.PENDING);
        }
    }

    /**
     * Determines whenever the current status can change status to the desired based on the rules
     *
     * <p>Rules:</p>
     * <p>Not Allowed:</p>
     * <ul>
     *     <li>If the current status is finalized see {@link SupplierPaymentStatus#isFinal()}</li>
     * </ul>
     * Allowed:
     * <ul>
     *     <li>{@code PENDING} -> {@code PAID} or {@code PARTIALLY_PAID} or {@code CANCELED}</li>
     *     <li>{@code PARTIALLY_PAID} -> {@code PAID} or {@code CANCELED}</li>
     * </ul>
     *
     * @param status The desired transition status
     * @return {@code true} If the status can be changed to the desired, otherwise {@code false}
     */
    public boolean canChangeStatusTo(SupplierPaymentStatus status) {
        if (this.status.isFinal()) return false;

        return switch (this.status) {
            case PENDING -> status == SupplierPaymentStatus.PAID ||
                    status == SupplierPaymentStatus.PARTIALLY_PAID ||
                    status == SupplierPaymentStatus.CANCELED;
            case PARTIALLY_PAID -> status == SupplierPaymentStatus.PAID || status == SupplierPaymentStatus.CANCELED;
            default -> false;
        };
    }
}
