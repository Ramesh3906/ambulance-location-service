package com.ambulance.security;

public record DriverPrincipal(Long driverId, String email, Long ambulanceId) {}