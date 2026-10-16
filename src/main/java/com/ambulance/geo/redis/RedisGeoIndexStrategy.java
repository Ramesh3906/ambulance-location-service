package com.ambulance.geo.redis;

import com.ambulance.config.RedisKeyProperties;
import com.ambulance.geo.*;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedisGeoIndexStrategy implements GeoIndexStrategy {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisGeoLocationStore locationStore;
    private final RedisKeyProperties keys;

    public RedisGeoIndexStrategy(RedisTemplate<String, Object> redisTemplate,
                                 RedisGeoLocationStore locationStore,
                                 RedisKeyProperties keys) {
        this.redisTemplate = redisTemplate;
        this.locationStore = locationStore;
        this.keys = keys;
    }

    @Override
    public GeoStrategyType type() {
        return GeoStrategyType.REDIS_GEO_RADIUS;
    }

    @Override
    public void indexLocation(Long entityId, GeoLocation location) {
        // GEOADD overwrites the member, so no previous-cell bookkeeping is needed.
        redisTemplate.opsForGeo().add(
                keys.getGeoKey(),
                new Point(location.longitude(), location.latitude()),
                entityId.toString());
        locationStore.save(entityId, location);
    }

    @Override
    public void removeLocation(Long entityId) {
        redisTemplate.opsForGeo().remove(keys.getGeoKey(), entityId.toString());
        locationStore.delete(entityId);
    }

    @Override
    public List<GeoCandidate> findWithinRadius(double latitude, double longitude, double radiusKm, int limit) {
        RedisGeoCommands.GeoSearchCommandArgs args = RedisGeoCommands.GeoSearchCommandArgs
                .newGeoSearchArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(limit);

        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo().search(
                keys.getGeoKey(),
                GeoReference.fromCoordinate(new Point(longitude, latitude)),
                new Distance(radiusKm, Metrics.KILOMETERS),
                args);

        if (results == null) {
            return List.of();
        }

        List<GeoCandidate> candidates = new ArrayList<>();
        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> result : results) {
            RedisGeoCommands.GeoLocation<Object> content = result.getContent();
            Point point = content.getPoint();
            if (point == null) {
                continue;
            }
            candidates.add(new GeoCandidate(
                    Long.valueOf(String.valueOf(content.getName())),
                    point.getY(),
                    point.getX(),
                    result.getDistance().getValue()));
        }
        return candidates;
    }

    @Override
    public GeoLocation getLocation(Long entityId) {
        return locationStore.find(entityId);
    }
}