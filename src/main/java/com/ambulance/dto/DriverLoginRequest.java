package com.ambulance.dto;

import jakarta.validation.constraints.NotBlank;

public record DriverLoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}