package com.ambulance.dto;

import com.ambulance.model.AmbulanceType;

public record NearbyAmbulanceDto(
        Long ambulanceId,
        String vehicleNumber,
        AmbulanceType ambulanceType,
        boolean nurse,
        boolean paramedic,
        boolean doctor,
        double distanceKm,
        long etaMinutes,
        double latitude,
        double longitude,
        double speedKmph,
        double heading,
        double accuracyMeters,
        String driverName,
        String driverContact
) {}
