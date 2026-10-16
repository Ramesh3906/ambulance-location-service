package com.ambulance.service;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(Long tripId) {
        super("Trip not found: " + tripId);
    }
}