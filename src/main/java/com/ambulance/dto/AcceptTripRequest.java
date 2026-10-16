package com.ambulance.dto;

import jakarta.validation.constraints.NotNull;

public record AcceptTripRequest(@NotNull Long ambulanceId) {}