package com.ambulance.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "driver")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "license_expiry")
    private LocalDate licenseExpiry;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ambulance_id", unique = true)
    private Ambulance ambulance;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPasswordHash() { return passwordHash; }
    public String getLicenseNumber() { return licenseNumber; }
    public LocalDate getLicenseExpiry() { return licenseExpiry; }
    public Ambulance getAmbulance() { return ambulance; }
    public boolean isActive() { return active; }
    public Instant getLastLoginAt() { return lastLoginAt; }

    public void setFullName(String v) { this.fullName = v; }
    public void setEmail(String v) { this.email = v; }
    public void setPhone(String v) { this.phone = v; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
    public void setLicenseNumber(String v) { this.licenseNumber = v; }
    public void setLicenseExpiry(LocalDate v) { this.licenseExpiry = v; }
    public void setAmbulance(Ambulance v) { this.ambulance = v; }
    public void setActive(boolean v) { this.active = v; }
    public void setLastLoginAt(Instant v) { this.lastLoginAt = v; }
}