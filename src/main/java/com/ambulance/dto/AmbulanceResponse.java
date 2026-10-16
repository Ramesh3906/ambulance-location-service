package com.ambulance.dto;

import com.ambulance.model.AmbulanceStatus;
import com.ambulance.model.AmbulanceType;

public record AmbulanceResponse(
        Long ambulanceId,
        String registrationNumber,
        String vehicleNumber,
        AmbulanceType ambulanceType,
        String vehicleModel,
        Integer capacity,
        Long providerId,
        boolean nurse,
        boolean paramedic,
        boolean doctor,
        AmbulanceStatus status,
        boolean active,
        Long driverId,
        String driverName,
        String driverContact
) {}