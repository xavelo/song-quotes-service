package com.xavelo.sqs.adapter.out.mysql.outbox;

import com.xavelo.sqs.application.domain.QuoteEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "quote_events")
public class QuoteEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuoteEventType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuoteEventStatus status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Integer attempts;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "available_at", nullable = false)
    private LocalDateTime availableAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public QuoteEventEntity() {
    }

    public QuoteEventEntity(QuoteEventType type, QuoteEventStatus status, String payload) {
        this.type = type;
        this.status = status;
        this.payload = payload;
        this.attempts = 0;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.availableAt == null) {
            this.availableAt = now;
        }
        if (this.attempts == null) {
            this.attempts = 0;
        }
        if (this.status == null) {
            this.status = QuoteEventStatus.PENDING;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public QuoteEventType getType() {
        return type;
    }

    public void setType(QuoteEventType type) {
        this.type = type;
    }

    public QuoteEventStatus getStatus() {
        return status;
    }

    public void setStatus(QuoteEventStatus status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public LocalDateTime getAvailableAt() {
        return availableAt;
    }

    public void setAvailableAt(LocalDateTime availableAt) {
        this.availableAt = availableAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
