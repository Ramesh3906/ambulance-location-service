package com.ambulance.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record DriverRegistrationRequest(
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Pattern(regexp = "\\d{10,15}") String phone,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 50) String licenseNumber,
        LocalDate licenseExpiry,
        Long ambulanceId
) {}