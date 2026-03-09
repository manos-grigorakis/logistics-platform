package com.manosgrigorakis.logisticsplatform.shipments.model;

import com.manosgrigorakis.logisticsplatform.quotes.model.Quote;
import com.manosgrigorakis.logisticsplatform.shipments.enums.ShipmentStatus;
import com.manosgrigorakis.logisticsplatform.shipments.enums.VehicleType;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@ToString(exclude = {"quote", "driver", "createdByUser", "truck", "trailer", "shipmentCargos"})
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(name = "number", unique = true, nullable = false, length = 13)
    private String number;

    @Column(name = "pickup", nullable = false)
    private LocalDateTime pickup;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", unique = true, nullable = false)
    private Quote quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    private Vehicle truck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trailer_id")
    private Vehicle trailer;

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentCargo> shipmentCargos = new ArrayList<>();

    public Shipment() {
    }

    @Builder
    public Shipment(
            String number,
            String notes,
            LocalDateTime pickup,
            Quote quote,
            User driver,
            User createdByUser,
            Vehicle truck,
            Vehicle trailer)
    {
        this.number = number;
        this.notes = notes;
        this.pickup = pickup;
        this.quote = quote;
        this.driver = driver;
        this.createdByUser = createdByUser;
        this.truck = truck;
        this.trailer = trailer;
    }

    // Constructor overloading for Copy Constructor
    public Shipment(
            Long id, String number, LocalDateTime pickup, String notes, User driver, Vehicle truck, Vehicle trailer
    ) {
        this.id = id;
        this.number = number;
        this.pickup = pickup;
        this.notes = notes;
        this.driver = driver;
        this.truck = truck;
        this.trailer = trailer;
    }

    // Copy Constructor
    public Shipment(Shipment another) {
        this(
                another.id, another.number, another.pickup,
                another.notes, another.driver, another.truck,
                another.trailer
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

    public boolean isEditable() {
        return this.status.isEditable();
    }

    public boolean isFinalized() {
        return this.status.isFinalized();
    }

    public boolean hasDriverRole() {
        return driver != null
                && driver.getRole() != null
                && "DRIVER".equals(driver.getRole().getName());
    }

    public boolean hasTruckType() {
        return truck != null && truck.getType() == VehicleType.TRUCK;
    }

    public boolean hasTrailerType() {
        return trailer != null && trailer.getType() == VehicleType.TRAILER;
    }

    public void addShipmentCargoItem(ShipmentCargo cargoItem) {
        this.shipmentCargos.add(cargoItem);
        cargoItem.setShipment(this);
    }
}
