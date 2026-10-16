package com.ambulance.geo;

public interface GeoIndexStrategySelector {
    GeoIndexStrategy active();
    GeoIndexStrategy select(GeoStrategyType requested);
}