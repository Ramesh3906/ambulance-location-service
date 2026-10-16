package com.ambulance.controller;

import com.ambulance.dto.*;
import com.ambulance.service.AmbulanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ambulances")
public class AmbulanceController {

    private final AmbulanceService ambulanceService;

    public AmbulanceController(AmbulanceService ambulanceService) {
        this.ambulanceService = ambulanceService;
    }

    @PostMapping
    public ResponseEntity<AmbulanceResponse> create(@Valid @RequestBody CreateAmbulanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ambulanceService.toResponse(ambulanceService.create(request)));
    }

    @GetMapping("/{ambulanceId}")
    public ResponseEntity<AmbulanceResponse> get(@PathVariable Long ambulanceId) {
        return ResponseEntity.ok(ambulanceService.get(ambulanceId));
    }

    @PatchMapping("/{ambulanceId}/status")
    public ResponseEntity<AmbulanceResponse> updateStatus(
            @PathVariable Long ambulanceId,
            @Valid @RequestBody UpdateAmbulanceStatusRequest request) {
        return ResponseEntity.ok(ambulanceService.updateStatus(ambulanceId, request));
    }
}