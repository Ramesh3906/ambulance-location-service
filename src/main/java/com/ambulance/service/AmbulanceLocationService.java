package com.ambulance.service;

import com.ambulance.config.GeoProperties;
import com.ambulance.dto.LocationUpdateRequest;
import com.ambulance.dto.LocationUpdateResponse;
import com.ambulance.event.LocationEventPublisher;
import com.ambulance.geo.GeoIndexStrategy;
import com.ambulance.geo.GeoLocation;
import com.ambulance.geo.GeoCellResolver;
import com.ambulance.model.Ambulance;
import com.ambulance.repository.AmbulanceRepository;
import com.ambulance.trip.TripTrackingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AmbulanceLocationService {

    private final AmbulanceRepository ambulanceRepository;
    private final GeoIndexStrategy geoIndexStrategy;
    private final LocationEventPublisher eventPublisher;
    private final GeoCellResolver geoCellResolver;
    private final TripTrackingService tripTrackingService;
    private final GeoProperties geoProperties;

    public AmbulanceLocationService(
            AmbulanceRepository ambulanceRepository,
            GeoIndexStrategy geoIndexStrategy,
            LocationEventPublisher eventPublisher,
            GeoCellResolver geoCellResolver,
            TripTrackingService tripTrackingService,
            GeoProperties geoProperties
    ) {
        this.ambulanceRepository = ambulanceRepository;
        this.geoIndexStrategy = geoIndexStrategy;
        this.eventPublisher = eventPublisher;
        this.geoCellResolver = geoCellResolver;
        this.geoProperties = geoProperties;
        this.tripTrackingService = tripTrackingService;
    }

    @Transactional(readOnly = true)
    public LocationUpdateResponse updateLocation(Long ambulanceId, LocationUpdateRequest request) {
        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new AmbulanceNotFoundException(ambulanceId));

        if (!ambulance.isActive()) {
            throw new IllegalStateException("Ambulance is inactive: " + ambulanceId);
        }
        double accuracy = request.accuracyMeters() == null ? 0.0 : request.accuracyMeters();
        if (geoProperties.getMaxAccuracyMeters() > 0 && accuracy > geoProperties.getMaxAccuracyMeters()) {
            // Keep the previous good fix rather than teleporting the ambulance.
            throw new IllegalStateException("GPS accuracy too low: " + accuracy + "m");
        }

        long timestamp = System.currentTimeMillis();

        GeoLocation location = new GeoLocation(
                ambulanceId,
                request.latitude(),
                request.longitude(),
                request.speedKmph() == null ? 0.0 : request.speedKmph(),
                request.heading() == null ? 0.0 : request.heading(),
                accuracy,
                timestamp
        );

        geoIndexStrategy.indexLocation(ambulanceId, location);
        eventPublisher.publishLocationUpdate(location);
        tripTrackingService.onLocationUpdate(location);

        return new LocationUpdateResponse(
                ambulanceId,
                location.latitude(),
                location.longitude(),
                geoCellResolver.cellFor(location.latitude(), location.longitude()),
                timestamp
        );
    }

    public void removeLocation(Long ambulanceId) {
        geoIndexStrategy.removeLocation(ambulanceId);
    }
}
