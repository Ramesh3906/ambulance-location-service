package com.ambulance.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "service_provider")
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "contact_number", length = 30)
    private String contactNumber;

    @Column(length = 150)
    private String email;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "emergency_service_available", nullable = false)
    private boolean emergencyServiceAvailable = true;

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
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public boolean isEmergencyServiceAvailable() { return emergencyServiceAvailable; }
    public void setName(String name) { this.name = name; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setActive(boolean active) { this.active = active; }
    public void setEmergencyServiceAvailable(boolean value) { this.emergencyServiceAvailable = value; }
}
