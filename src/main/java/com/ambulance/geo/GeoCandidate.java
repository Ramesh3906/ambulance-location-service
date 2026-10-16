package com.ambulance.geo;

public record GeoCandidate(
        Long entityId,
        double latitude,
        double longitude,
        double approxDistanceKm
) {}
