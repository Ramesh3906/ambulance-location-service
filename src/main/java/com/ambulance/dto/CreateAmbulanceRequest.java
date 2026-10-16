package com.ambulance.dto;

import com.ambulance.model.AmbulanceType;
import jakarta.validation.constraints.*;

public record CreateAmbulanceRequest(
        @NotBlank @Size(max = 50) String registrationNumber,
        @NotBlank @Size(max = 50) String vehicleNumber,
        @NotNull AmbulanceType ambulanceType,
        @Size(max = 100) String vehicleModel,
        @Min(1) @Max(10) Integer capacity,
        Long providerId,
        boolean nurse,
        boolean paramedic,
        boolean doctor
) {}