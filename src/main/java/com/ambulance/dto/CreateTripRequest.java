package com.ambulance.dto;

import com.ambulance.model.AmbulanceType;
import jakarta.validation.constraints.*;

public record CreateTripRequest(
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double pickupLatitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double pickupLongitude,
        String pickupAddress,
        @DecimalMin("-90.0") @DecimalMax("90.0") Double dropLatitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") Double dropLongitude,
        String dropAddress,
        @Size(max = 150) String patientName,
        @Size(max = 30) String patientContact,
        AmbulanceType requiredType
) {
}