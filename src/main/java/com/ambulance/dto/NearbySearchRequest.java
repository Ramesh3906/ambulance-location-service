package com.ambulance.dto;

import com.ambulance.geo.GeoStrategyType;
import com.ambulance.model.AmbulanceType;
import jakarta.validation.constraints.*;

public record NearbySearchRequest(
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        @DecimalMin("0.1") @DecimalMax("50.0") Double radiusKm,
        @Min(1) @Max(50) Integer limit,
        AmbulanceType requiredType,
        Boolean requireDoctor,
        Boolean requireNurse,
        Boolean requireParamedic,
        GeoStrategyType strategy
) {}