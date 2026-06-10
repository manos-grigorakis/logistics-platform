package com.manosgrigorakis.logisticsplatform.cmr.model;

import com.manosgrigorakis.logisticsplatform.cmr.enums.CmrStatus;
import com.manosgrigorakis.logisticsplatform.shipments.model.Shipment;
import jakarta.persistence.*;
import lombok.Builder;
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
    private CmrStatus status;

    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl;

    @Column(name = "sender_signed", nullable = false)
    private boolean senderSigned;

    @Column(name = "carrier_signed", nullable = false)
    private boolean carrierSigned;

    @Column(name = "consignee_signed", nullable = false)
    private boolean consigneeSigned;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false, unique = true)
    private Shipment shipment;

    public CmrDocument() {
    }

    @Builder
    public CmrDocument(
            String number,
            CmrStatus status,
            String fileUrl,
            Shipment shipment,
            boolean senderSigned,
            boolean carrierSigned,
            boolean consigneeSigned
    ) {
        this.number = number;
        this.status = status;
        this.fileUrl = fileUrl;
        this.shipment = shipment;
        this.senderSigned = senderSigned;
        this.carrierSigned = carrierSigned;
        this.consigneeSigned = consigneeSigned;
    }

    // Constructor overloading for Copy Constructor
    public CmrDocument(
            Long id,
            String number,
            CmrStatus status,
            String fileUrl,
            boolean senderSigned,
            boolean carrierSigned,
            boolean consigneeSigned
    ) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.fileUrl = fileUrl;
        this.senderSigned = senderSigned;
        this.carrierSigned = carrierSigned;
        this.consigneeSigned = consigneeSigned;
    }

    // Copy Constructor
    public CmrDocument(CmrDocument another) {
        this(
                another.id,
                another.number,
                another.status,
                another.fileUrl,
                another.senderSigned,
                another.carrierSigned,
                another.consigneeSigned
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

    /**
     * Checks whenever the CMR status can change from its current value
     * Rules: </br>
     *  Not Allowed:
     *      - Finalized statuses cannot be changed </br>
     *  Allowed:
     *      - {@code GENERATED -> SIGNED}
     *      - {@code GENERATED -> CANCELLED}
     * @param status of the target
     * @return {@code true} if status can be changed, otherwise {@code false}
     */
    public Boolean canChangeStatusTo(CmrStatus status) {
        if (this.status.isFinal()) return false;
        return status.isFinal();
    }

    /**
     * Change the CMR status to the given target status applying business rules. Uses
     * {@link #canChangeStatusTo(CmrStatus)} to apply business rules
     *
     * @param status The status of the target
     * @throws IllegalStateException If business rules of status doesn't allow this action
     */
    public void changeStatusTo(CmrStatus status) throws IllegalStateException {
        if (!canChangeStatusTo(status)) {
            throw new IllegalStateException(
                    "Invalid status transition: " + this.status + " -> " + status
            );
        }

        this.status = status;
    }

    /**
     * Mark a CMR document as signed
     *
     * <ul>
     *     <li>Sets: {@link #senderSigned}, {@link #carrierSigned} and {@link #consigneeSigned} to {@code true}</li>
     *     <li>Update CMR document status to: {@link CmrStatus#SIGNED}</li>
     * </ul>
     */
    public void markCmrDocumentAsSigned() {
        this.senderSigned = true;
        this.carrierSigned = true;
        this.consigneeSigned = true;
        this.status = CmrStatus.SIGNED;
    }
}
