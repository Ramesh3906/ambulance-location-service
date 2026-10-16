package com.ambulance.eta;

public interface EtaCalculator {
    EtaResult calculate(
            double fromLatitude,
            double fromLongitude,
            double toLatitude,
            double toLongitude,
            double currentSpeedKmph
    );
}
