package com.ambulance.controller;

import com.ambulance.dto.*;
import com.ambulance.security.DriverPrincipal;
import com.ambulance.service.DriverAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverAuthController {

    private final DriverAuthService driverAuthService;

    public DriverAuthController(DriverAuthService driverAuthService) {
        this.driverAuthService = driverAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody DriverRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody DriverLoginRequest request) {
        return ResponseEntity.ok(driverAuthService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(driverAuthService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal DriverPrincipal principal) {
        driverAuthService.logout(principal.driverId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<DriverProfileResponse> me(@AuthenticationPrincipal DriverPrincipal principal) {
        return ResponseEntity.ok(driverAuthService.profile(principal.driverId()));
    }

    @PostMapping("/me/ambulance")
    public ResponseEntity<DriverProfileResponse> assignAmbulance(
            @AuthenticationPrincipal DriverPrincipal principal,
            @Valid @RequestBody AssignAmbulanceRequest request) {
        return ResponseEntity.ok(driverAuthService.assignAmbulance(principal.driverId(), request));
    }

    @DeleteMapping("/me/ambulance")
    public ResponseEntity<DriverProfileResponse> unassignAmbulance(
            @AuthenticationPrincipal DriverPrincipal principal) {
        return ResponseEntity.ok(driverAuthService.unassignAmbulance(principal.driverId()));
    }
}