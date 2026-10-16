package com.ambulance.service;

import com.ambulance.config.GeoProperties;
import com.ambulance.geo.GeoIndexStrategy;
import com.ambulance.model.Ambulance;
import com.ambulance.repository.AmbulanceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StaleLocationCleanupService {

    private final AmbulanceRepository ambulanceRepository;
    private final GeoIndexStrategy geoIndexStrategy;
    private final GeoProperties geoProperties;

    public StaleLocationCleanupService(
            AmbulanceRepository ambulanceRepository,
            GeoIndexStrategy geoIndexStrategy,
            GeoProperties geoProperties
    ) {
        this.ambulanceRepository = ambulanceRepository;
        this.geoIndexStrategy = geoIndexStrategy;
        this.geoProperties = geoProperties;
    }

    @Scheduled(fixedDelayString = "${ambulance.geo.stale-cleanup-ms:60000}")
    public void cleanup() {
        // This baseline intentionally keeps cleanup bounded to known active ambulances.
        // For very large fleets, maintain a Redis sorted set by last-seen timestamp instead.
        List<Ambulance> ambulances = ambulanceRepository.findAll();

        long cutoff = System.currentTimeMillis() - geoProperties.getLocationTtlSeconds() * 1000L;
        for (Ambulance ambulance : ambulances) {
            var location = geoIndexStrategy.getLocation(ambulance.getId());
            if (location != null && location.updatedAt() < cutoff) {
                geoIndexStrategy.removeLocation(ambulance.getId());
            }
        }
    }
}
