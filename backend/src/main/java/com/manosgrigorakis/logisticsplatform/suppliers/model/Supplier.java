package com.manosgrigorakis.logisticsplatform.suppliers.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@SQLDelete(sql = "UPDATE suppliers SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted=false")
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

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = Boolean.FALSE;

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

    public void addSupplierPayment(SupplierPayment supplierPayment) {
        this.supplierPayments.add(supplierPayment);
    }

    public void removeSupplierPayment(SupplierPayment supplierPayment) {
        this.supplierPayments.remove(supplierPayment);
    }
}
