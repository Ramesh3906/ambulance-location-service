package com.ambulance.geo;

import com.ambulance.config.GeoProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class GeoIndexStrategyRouter implements GeoIndexStrategy, GeoIndexStrategySelector {

    private final Map<GeoStrategyType, GeoIndexStrategy> strategies = new EnumMap<>(GeoStrategyType.class);
    private final GeoProperties properties;

    public GeoIndexStrategyRouter(List<GeoIndexStrategy> strategies, GeoProperties properties) {
        this.properties = properties;
        for (GeoIndexStrategy strategy : strategies) {
            if (strategy != this) {
                this.strategies.put(strategy.type(), strategy);
            }
        }
    }

    @Override
    public GeoIndexStrategy active() {
        GeoIndexStrategy strategy = strategies.get(properties.getStrategy());
        if (strategy == null) {
            throw new IllegalStateException("No GeoIndexStrategy registered for " + properties.getStrategy());
        }
        return strategy;
    }

    @Override
    public GeoIndexStrategy select(GeoStrategyType requested) {
        if (requested == null || !properties.isAllowRequestOverride()) {
            return active();
        }
        return strategies.getOrDefault(requested, active());
    }

    @Override
    public GeoStrategyType type() {
        return properties.getStrategy();
    }

    @Override
    public void indexLocation(Long entityId, GeoLocation location) {
        if (properties.isDualWrite()) {
            strategies.values().forEach(strategy -> strategy.indexLocation(entityId, location));
        } else {
            active().indexLocation(entityId, location);
        }
    }

    @Override
    public void removeLocation(Long entityId) {
        // Always purge every index so a later strategy switch cannot resurrect stale entries.
        strategies.values().forEach(strategy -> strategy.removeLocation(entityId));
    }

    @Override
    public List<GeoCandidate> findWithinRadius(double latitude, double longitude, double radiusKm, int limit) {
        return active().findWithinRadius(latitude, longitude, radiusKm, limit);
    }

    @Override
    public GeoLocation getLocation(Long entityId) {
        return active().getLocation(entityId);
    }
}