package com.ambulance.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ambulance")
public class Ambulance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number", nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 50)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "ambulance_type", nullable = false, length = 30)
    private AmbulanceType ambulanceType;

    @Column(name = "vehicle_model", length = 100)
    private String vehicleModel;

    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ServiceProvider provider;

    @Column(name = "has_nurse", nullable = false)
    private boolean nurse;

    @Column(name = "has_paramedic", nullable = false)
    private boolean paramedic;

    @Column(name = "has_doctor", nullable = false)
    private boolean doctor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AmbulanceStatus status;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "driver_name", length = 150)
    private String driverName;

    @Column(name = "driver_contact", length = 30)
    private String driverContact;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @OneToOne(mappedBy = "ambulance", fetch = FetchType.LAZY)
    private Driver driver;

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

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public Long getId() { return id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getVehicleNumber() { return vehicleNumber; }
    public AmbulanceType getAmbulanceType() { return ambulanceType; }
    public String getVehicleModel() { return vehicleModel; }
    public Integer getCapacity() { return capacity; }
    public ServiceProvider getProvider() { return provider; }
    public boolean isNurse() { return nurse; }
    public boolean isParamedic() { return paramedic; }
    public boolean isDoctor() { return doctor; }
    public AmbulanceStatus getStatus() { return status; }
    public boolean isActive() { return active; }
    public String getDriverName() { return driverName; }
    public String getDriverContact() { return driverContact; }

    public void setRegistrationNumber(String value) { this.registrationNumber = value; }
    public void setVehicleNumber(String value) { this.vehicleNumber = value; }
    public void setAmbulanceType(AmbulanceType value) { this.ambulanceType = value; }
    public void setVehicleModel(String value) { this.vehicleModel = value; }
    public void setCapacity(Integer value) { this.capacity = value; }
    public void setProvider(ServiceProvider value) { this.provider = value; }
    public void setNurse(boolean value) { this.nurse = value; }
    public void setParamedic(boolean value) { this.paramedic = value; }
    public void setDoctor(boolean value) { this.doctor = value; }
    public void setStatus(AmbulanceStatus value) { this.status = value; }
    public void setActive(boolean value) { this.active = value; }
    public void setDriverName(String value) { this.driverName = value; }
    public void setDriverContact(String value) { this.driverContact = value; }
}
