package com.ambulance.geo;

public record GeoLocation(
        Long entityId,
        double latitude,
        double longitude,
        double speedKmph,
        double heading,
        double accuracyMeters,
        long updatedAt
) {}
