package com.ambulance.dto;

import jakarta.validation.constraints.*;

public record LocationUpdateRequest(
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        @DecimalMin("0.0") @DecimalMax("300.0") Double speedKmph,
        @DecimalMin("0.0") @DecimalMax("360.0") Double heading,
        @DecimalMin("0.0") @DecimalMax("1000.0") Double accuracyMeters
) {}
