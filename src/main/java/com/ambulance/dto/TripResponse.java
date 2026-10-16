package com.ambulance.dto;

import com.ambulance.model.TripStatus;

import java.time.Instant;

public record TripResponse(
        Long tripId,
        String reference,
        TripStatus status,
        Long ambulanceId,
        String vehicleNumber,
        String driverName,
        String driverContact,
        double pickupLatitude,
        double pickupLongitude,
        Double dropLatitude,
        Double dropLongitude,
        Instant acceptedAt,
        Instant arrivedAt,
        Instant startedAt,
        Instant completedAt,
        Instant cancelledAt,
        Double lastRemainingKm,
        Long lastEtaMinutes,
        String trackingUrl,
        String trackingStreamUrl
) {}