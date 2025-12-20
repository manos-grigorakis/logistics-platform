package com.manosgrigorakis.logisticsplatform.shipments.model;

import com.manosgrigorakis.logisticsplatform.shipments.enums.VehicleType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@ToString
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "plate", unique = true, nullable = false, length = 8)
    private String plate;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Vehicle() {
    }

    @Builder
    public Vehicle(String brand, String plate, VehicleType type) {
        this.brand = brand;
        this.plate = plate;
        this.type = type;
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
