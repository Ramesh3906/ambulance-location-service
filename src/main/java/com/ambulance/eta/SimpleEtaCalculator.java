package com.ambulance.eta;

import com.ambulance.geo.DistanceCalculator;
import org.springframework.stereotype.Component;

@Component
public class SimpleEtaCalculator implements EtaCalculator {

    private final DistanceCalculator distanceCalculator;

    public SimpleEtaCalculator(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public EtaResult calculate(
            double fromLatitude,
            double fromLongitude,
            double toLatitude,
            double toLongitude,
            double currentSpeedKmph
    ) {
        double distance = distanceCalculator.distanceKm(
                fromLatitude, fromLongitude, toLatitude, toLongitude
        );

        // Placeholder ETA only. Replace with Google/HERE/Mapbox routing in production.
        double effectiveSpeed = currentSpeedKmph > 5 ? currentSpeedKmph : 25.0;
        long minutes = Math.max(1, Math.round((distance / effectiveSpeed) * 60));

        return new EtaResult(distance, minutes);
    }
}
