package com.ambulance.dto;

import jakarta.validation.constraints.Size;

public record CancelTripRequest(@Size(max = 255) String reason) {}