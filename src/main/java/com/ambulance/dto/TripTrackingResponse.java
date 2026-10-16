package com.ambulance.dto;

import com.ambulance.model.TripStatus;
import com.ambulance.model.TripTarget;

import java.time.Instant;

public record TripTrackingResponse(
        Long tripId,
        String reference,
        TripStatus status,
        TripTarget target,
        Long ambulanceId,
        String vehicleNumber,
        String driverName,
        String driverContact,
        double ambulanceLatitude,
        double ambulanceLongitude,
        double speedKmph,
        double heading,
        double targetLatitude,
        double targetLongitude,
        double remainingDistanceKm,
        long etaMinutes,
        Instant etaAt,
        long locationUpdatedAt,
        boolean locationStale
) {}