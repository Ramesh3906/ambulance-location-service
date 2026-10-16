package com.ambulance.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "driver_refresh_token")
public class DriverRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public boolean isUsable() {
        return !revoked && expiresAt.isAfter(Instant.now());
    }

    public Long getId() { return id; }
    public Driver getDriver() { return driver; }
    public String getTokenHash() { return tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }

    public void setDriver(Driver v) { this.driver = v; }
    public void setTokenHash(String v) { this.tokenHash = v; }
    public void setExpiresAt(Instant v) { this.expiresAt = v; }
    public void setRevoked(boolean v) { this.revoked = v; }
}