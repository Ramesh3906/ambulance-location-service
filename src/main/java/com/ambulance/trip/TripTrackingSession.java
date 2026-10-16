package com.ambulance.trip;

import com.ambulance.model.TripStatus;
import com.ambulance.model.TripTarget;

public record TripTrackingSession(
        Long tripId,
        String reference,
        TripStatus status,
        TripTarget target,
        Long ambulanceId,
        String vehicleNumber,
        String driverName,
        String driverContact,
        double targetLatitude,
        double targetLongitude
) {}