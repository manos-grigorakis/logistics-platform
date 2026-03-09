package com.manosgrigorakis.logisticsplatform.shipments.model;

import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentCargoUnit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments_cargo")
@Getter
@Setter
@ToString(exclude = {"shipment"})
public class ShipmentCargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "description", length = 50, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private ShipmentCargoUnit unit;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "weight_kg", precision = 10, scale = 2, nullable = false)
    private BigDecimal weightKg;

    @Column(name = "volume_m3", precision = 10, scale = 2)
    private BigDecimal volumeM3;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    public ShipmentCargo() {
    }

    public ShipmentCargo(
            String description,
            ShipmentCargoUnit unit,
            Integer quantity,
            BigDecimal weightKg,
            BigDecimal volumeM3
    ) {
        this.description = description;
        this.unit = unit;
        this.quantity = quantity;
        this.weightKg = weightKg;
        this.volumeM3 = volumeM3;
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
