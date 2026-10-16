package com.ambulance.repository;

import com.ambulance.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByPhone(String phone);
    Optional<Driver> findByAmbulanceId(Long ambulanceId);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByAmbulanceId(Long ambulanceId);
}