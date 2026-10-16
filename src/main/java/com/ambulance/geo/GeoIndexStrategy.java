package com.ambulance.geo;

import java.util.List;

public interface GeoIndexStrategy {
    GeoStrategyType type();

    void indexLocation(Long entityId, GeoLocation location);

    void removeLocation(Long entityId);

    List<GeoCandidate> findWithinRadius(double latitude, double longitude, double radiusKm, int limit);

    GeoLocation getLocation(Long entityId);
}