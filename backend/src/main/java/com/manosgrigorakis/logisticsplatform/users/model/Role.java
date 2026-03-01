package com.manosgrigorakis.logisticsplatform.users.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    @Column(name = "description", nullable = true, length = 255)
    private String description;

    @Column(name = "is_editable", nullable = false)
    private boolean isEditable = true;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Role() {
    }

    @Builder
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Constructor overloading for using Copy Constructor
    public Role(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isEditable = isEditable;
    }

    // Copy Constructor
    public Role(Role another) {
        this(another.id, another.name, another.description);
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
