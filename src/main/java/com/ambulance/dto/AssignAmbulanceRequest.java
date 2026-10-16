package com.ambulance.dto;

import jakarta.validation.Valid;

public record AssignAmbulanceRequest(
        Long ambulanceId,
        @Valid CreateAmbulanceRequest ambulance
) {}