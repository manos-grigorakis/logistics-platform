package com.manosgrigorakis.logisticsplatform.cmr.model;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "cmr_documents")
@Getter
@Setter
@ToString(exclude = {"shipment"})
public class CmrDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "number", unique = true, nullable = false, length = 14)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CmrStatus status = CmrStatus.GENERATED;

    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "signed_by")
    private String signedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false, unique = true)
    private Shipment shipment;

    public CmrDocument() {
    }

    public CmrDocument(
            String number,
            CmrStatus status,
            String fileUrl,
            LocalDateTime signedAt,
            String signedBy
    ) {
        this.number = number;
        this.status = status;
        this.fileUrl = fileUrl;
        this.signedAt = signedAt;
        this.signedBy = signedBy;
    }

    // Constructor overloading for Copy Constructor
    public CmrDocument(
            Long id,
            String number,
            CmrStatus status,
            String fileUrl,
            LocalDateTime signedAt,
            String signedBy
    ) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.fileUrl = fileUrl;
        this.signedAt = signedAt;
        this.signedBy = signedBy;
    }

    // Copy Constructor
    public CmrDocument(CmrDocument another) {
        this(
                another.id,
                another.number,
                another.status,
                another.fileUrl,
                another.signedAt,
                another.signedBy
        );
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
