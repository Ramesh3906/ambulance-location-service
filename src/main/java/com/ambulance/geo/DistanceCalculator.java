package com.ambulance.geo;

public interface DistanceCalculator {
    double distanceKm(double fromLat, double fromLng, double toLat, double toLng);
}
