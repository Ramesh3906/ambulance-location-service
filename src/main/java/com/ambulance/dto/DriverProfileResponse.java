package com.ambulance.dto;

import com.ambulance.model.AmbulanceStatus;
import com.ambulance.model.AmbulanceType;

import java.time.Instant;
import java.time.LocalDate;

public record DriverProfileResponse(
        Long driverId,
        String fullName,
        String email,
        String phone,
        String licenseNumber,
        LocalDate licenseExpiry,
        boolean active,
        Instant lastLoginAt,
        Long ambulanceId,
        String vehicleNumber,
        String registrationNumber,
        AmbulanceType ambulanceType,
        AmbulanceStatus ambulanceStatus
) {}