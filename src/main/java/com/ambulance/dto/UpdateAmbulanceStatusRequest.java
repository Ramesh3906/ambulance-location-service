package com.ambulance.dto;

import com.ambulance.model.AmbulanceStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAmbulanceStatusRequest(@NotNull AmbulanceStatus status) {}