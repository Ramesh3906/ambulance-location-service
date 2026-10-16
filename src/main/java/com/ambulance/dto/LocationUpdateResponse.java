package com.ambulance.dto;

public record LocationUpdateResponse(
        Long ambulanceId,
        double latitude,
        double longitude,
        String h3Cell,
        long updatedAt
) {}
