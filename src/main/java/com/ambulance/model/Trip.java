package com.ambulance.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "trip")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String reference;

    @Column(name = "patient_name", length = 150)
    private String patientName;

    @Column(name = "patient_contact", length = 30)
    private String patientContact;

    @Column(name = "pickup_latitude", nullable = false)
    private double pickupLatitude;

    @Column(name = "pickup_longitude", nullable = false)
    private double pickupLongitude;

    @Column(name = "pickup_address", length = 255)
    private String pickupAddress;

    @Column(name = "drop_latitude")
    private Double dropLatitude;

    @Column(name = "drop_longitude")
    private Double dropLongitude;

    @Column(name = "drop_address", length = 255)
    private String dropAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_type", length = 30)
    private AmbulanceType requiredType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ambulance_id")
    private Ambulance ambulance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TripStatus status = TripStatus.REQUESTED;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "arrived_at")
    private Instant arrivedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    /** Last ETA snapshot persisted on lifecycle events only, never per GPS packet. */
    @Column(name = "last_remaining_km")
    private Double lastRemainingKm;

    @Column(name = "last_eta_minutes")
    private Long lastEtaMinutes;

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

    /** Before pickup the driver heads to the patient; afterwards to the drop point. */
    public TripTarget currentTarget() {
        if (status == TripStatus.IN_PROGRESS && dropLatitude != null && dropLongitude != null) {
            return TripTarget.DROP;
        }
        return TripTarget.PICKUP;
    }

    public double targetLatitude() {
        return currentTarget() == TripTarget.DROP ? dropLatitude : pickupLatitude;
    }

    public double targetLongitude() {
        return currentTarget() == TripTarget.DROP ? dropLongitude : pickupLongitude;
    }

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public String getPatientName() { return patientName; }
    public String getPatientContact() { return patientContact; }
    public double getPickupLatitude() { return pickupLatitude; }
    public double getPickupLongitude() { return pickupLongitude; }
    public String getPickupAddress() { return pickupAddress; }
    public Double getDropLatitude() { return dropLatitude; }
    public Double getDropLongitude() { return dropLongitude; }
    public String getDropAddress() { return dropAddress; }
    public AmbulanceType getRequiredType() { return requiredType; }
    public Ambulance getAmbulance() { return ambulance; }
    public TripStatus getStatus() { return status; }
    public Instant getAcceptedAt() { return acceptedAt; }
    public Instant getArrivedAt() { return arrivedAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
    public String getCancelReason() { return cancelReason; }
    public Double getLastRemainingKm() { return lastRemainingKm; }
    public Long getLastEtaMinutes() { return lastEtaMinutes; }
    public Instant getCreatedAt() { return createdAt; }

    public void setReference(String value) { this.reference = value; }
    public void setPatientName(String value) { this.patientName = value; }
    public void setPatientContact(String value) { this.patientContact = value; }
    public void setPickupLatitude(double value) { this.pickupLatitude = value; }
    public void setPickupLongitude(double value) { this.pickupLongitude = value; }
    public void setPickupAddress(String value) { this.pickupAddress = value; }
    public void setDropLatitude(Double value) { this.dropLatitude = value; }
    public void setDropLongitude(Double value) { this.dropLongitude = value; }
    public void setDropAddress(String value) { this.dropAddress = value; }
    public void setRequiredType(AmbulanceType value) { this.requiredType = value; }
    public void setAmbulance(Ambulance value) { this.ambulance = value; }
    public void setStatus(TripStatus value) { this.status = value; }
    public void setAcceptedAt(Instant value) { this.acceptedAt = value; }
    public void setArrivedAt(Instant value) { this.arrivedAt = value; }
    public void setStartedAt(Instant value) { this.startedAt = value; }
    public void setCompletedAt(Instant value) { this.completedAt = value; }
    public void setCancelledAt(Instant value) { this.cancelledAt = value; }
    public void setCancelReason(String value) { this.cancelReason = value; }
    public void setLastRemainingKm(Double value) { this.lastRemainingKm = value; }
    public void setLastEtaMinutes(Long value) { this.lastEtaMinutes = value; }
}