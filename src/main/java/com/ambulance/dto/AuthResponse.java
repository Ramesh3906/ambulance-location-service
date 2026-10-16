package com.ambulance.dto;

public record AuthResponse(
        Long driverId,
        String fullName,
        Long ambulanceId,
        String vehicleNumber,
        boolean ambulanceRegistered,
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds
) {}