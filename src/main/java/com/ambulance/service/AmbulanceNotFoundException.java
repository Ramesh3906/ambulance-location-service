package com.ambulance.service;

public class AmbulanceNotFoundException extends RuntimeException {
    public AmbulanceNotFoundException(Long ambulanceId) {
        super("Ambulance not found: " + ambulanceId);
    }
}
