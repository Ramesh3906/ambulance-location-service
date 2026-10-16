package com.ambulance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ambulance.trip")
public class TripProperties {

    /**
     * Lifetime of a live-tracking session in Redis.
     */
    private long trackingTtlSeconds = 21_600;

    /**
     * Driver is considered arrived within this distance of the target.
     */
    private double arrivalThresholdKm = 0.15;

    public long getTrackingTtlSeconds() {
        return trackingTtlSeconds;
    }

    public void setTrackingTtlSeconds(long value) {
        this.trackingTtlSeconds = value;
    }

    public double getArrivalThresholdKm() {
        return arrivalThresholdKm;
    }

    public void setArrivalThresholdKm(double value) {
        this.arrivalThresholdKm = value;
    }
}