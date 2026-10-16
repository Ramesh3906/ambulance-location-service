package com.ambulance.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HaversineDistanceCalculatorTest {

    private final HaversineDistanceCalculator calculator = new HaversineDistanceCalculator();

    @Test
    void samePointShouldHaveZeroDistance() {
        assertEquals(
                0.0,
                calculator.distanceKm(12.9716, 77.5946, 12.9716, 77.5946),
                0.000001
        );
    }

    @Test
    void knownDistanceShouldBeReasonable() {
        double distance = calculator.distanceKm(
                12.9716, 77.5946,
                12.9352, 77.6245
        );

        assertEquals(5.0, distance, 1.0);
    }
}
