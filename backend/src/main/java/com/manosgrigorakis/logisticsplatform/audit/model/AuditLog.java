package com.manosgrigorakis.logisticsplatform.audit.model;

import com.manosgrigorakis.logisticsplatform.audit.enums.AuditAction;
import com.manosgrigorakis.logisticsplatform.users.model.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@ToString(exclude = {"user"})
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "entity_type", length = 50, nullable = false)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", length = 50, nullable = false)
    private AuditAction action;

    @Column(name = "changes", columnDefinition = "longtext")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> changes;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public AuditLog() {
    }

    @Builder
    public AuditLog(
            String entityType, Long entityId, AuditAction action,
            Map<String, Object> changes, String notes, String ipAddress,
            String userAgent
    ) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.changes = changes;
        this.notes = notes;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
