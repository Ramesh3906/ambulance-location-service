package com.ambulance.controller;

import com.ambulance.dto.LocationUpdateRequest;
import com.ambulance.dto.LocationUpdateResponse;
import com.ambulance.service.AmbulanceLocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ambulances")
public class AmbulanceLocationController {

    private final AmbulanceLocationService locationService;

    public AmbulanceLocationController(AmbulanceLocationService locationService) {
        this.locationService = locationService;
    }

    @PutMapping("/{ambulanceId}/location")
    public ResponseEntity<LocationUpdateResponse> updateLocation(
            @PathVariable Long ambulanceId,
            @Valid @RequestBody LocationUpdateRequest request
    ) {
        return ResponseEntity.ok(locationService.updateLocation(ambulanceId, request));
    }

    @DeleteMapping("/{ambulanceId}/location")
    public ResponseEntity<Void> removeLocation(@PathVariable Long ambulanceId) {
        locationService.removeLocation(ambulanceId);
        return ResponseEntity.noContent().build();
    }
}
