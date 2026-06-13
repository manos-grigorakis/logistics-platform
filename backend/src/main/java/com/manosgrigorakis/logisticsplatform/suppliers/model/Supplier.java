package com.manosgrigorakis.logisticsplatform.suppliers.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "suppliers")
@Entity
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "company_name",unique = true, nullable = false, length = 100)
    private String companyName;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<SupplierPayment> supplierPayments = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Builder
    public Supplier(String companyName, String email) {
        this.companyName = companyName;
        this.email = email;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
