package com.hospitalappointment.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String action;

    @Column(length = 100)
    private String username;

    @Column(name = "entity_type", nullable = false, length = 120)
    private String entityType;

    @Column(name = "entity_id", length = 120)
    private String entityId;

    @Column(length = 1000)
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
