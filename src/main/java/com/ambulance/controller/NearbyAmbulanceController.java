package com.ambulance.controller;

import com.ambulance.dto.NearbyAmbulanceDto;
import com.ambulance.dto.NearbySearchRequest;
import com.ambulance.service.NearbyAmbulanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ambulances")
public class NearbyAmbulanceController {

    private final NearbyAmbulanceService nearbyAmbulanceService;

    public NearbyAmbulanceController(NearbyAmbulanceService nearbyAmbulanceService) {
        this.nearbyAmbulanceService = nearbyAmbulanceService;
    }

    @PostMapping("/nearby")
    public List<NearbyAmbulanceDto> findNearby(
            @Valid @RequestBody NearbySearchRequest request
    ) {
        return nearbyAmbulanceService.findNearby(request);
    }
}
