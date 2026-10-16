package com.ambulance.geo.h3;

import com.ambulance.config.GeoProperties;
import com.ambulance.config.RedisKeyProperties;
import com.ambulance.geo.*;
import com.ambulance.geo.redis.RedisGeoLocationStore;
import com.uber.h3core.H3Core;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class H3GeoIndexStrategy implements GeoIndexStrategy {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DistanceCalculator distanceCalculator;
    private final RedisGeoLocationStore locationStore;
    private final GeoProperties properties;
    private final RedisKeyProperties keys;
    private final H3Core h3;

    public H3GeoIndexStrategy(RedisTemplate<String, Object> redisTemplate,
                              DistanceCalculator distanceCalculator,
                              RedisGeoLocationStore locationStore,
                              GeoProperties properties,
                              RedisKeyProperties keys) throws IOException {
        this.redisTemplate = redisTemplate;
        this.distanceCalculator = distanceCalculator;
        this.locationStore = locationStore;
        this.properties = properties;
        this.keys = keys;
        this.h3 = H3Core.newInstance();
    }

    @Override
    public GeoStrategyType type() {
        return GeoStrategyType.H3_GRID_DISK;
    }

    @Override
    public void indexLocation(Long entityId, GeoLocation location) {
        String newCell = Long.toUnsignedString(
                h3.latLngToCell(location.latitude(), location.longitude(), properties.getH3Resolution()));

        String entityKey = keys.getEntityCellKeyPrefix() + entityId;
        Object oldCellValue = redisTemplate.opsForValue().get(entityKey);
        String oldCell = oldCellValue == null ? null : String.valueOf(oldCellValue);

        if (oldCell != null && !oldCell.equals(newCell)) {
            redisTemplate.opsForSet().remove(keys.getCellKeyPrefix() + oldCell, entityId.toString());
        }

        redisTemplate.opsForSet().add(keys.getCellKeyPrefix() + newCell, entityId.toString());
        redisTemplate.opsForValue().set(entityKey, newCell);
        locationStore.save(entityId, location);
    }

    @Override
    public void removeLocation(Long entityId) {
        String entityKey = keys.getEntityCellKeyPrefix() + entityId;
        Object oldCellValue = redisTemplate.opsForValue().get(entityKey);
        if (oldCellValue != null) {
            redisTemplate.opsForSet()
                    .remove(keys.getCellKeyPrefix() + oldCellValue, entityId.toString());
        }
        redisTemplate.delete(entityKey);
        locationStore.delete(entityId);
    }

    @Override
    public List<GeoCandidate> findWithinRadius(double latitude, double longitude, double radiusKm, int limit) {
        long originCell = h3.latLngToCell(latitude, longitude, properties.getH3Resolution());

        List<Long> cells = h3.gridDisk(originCell, ringsFor(radiusKm));

        List<String> cellKeys = cells.stream()
                .map(cell -> keys.getCellKeyPrefix() + Long.toUnsignedString(cell))
                .collect(Collectors.toList());

        // Single SUNION instead of one SMEMBERS per cell.
        Set<Object> members = redisTemplate.opsForSet().union(cellKeys);
        if (members == null || members.isEmpty()) {
            return List.of();
        }

        return members.stream()
                .map(member -> Long.valueOf(String.valueOf(member)))
                .map(locationStore::find)
                .filter(Objects::nonNull)
                .map(location -> new GeoCandidate(
                        location.entityId(),
                        location.latitude(),
                        location.longitude(),
                        distanceCalculator.distanceKm(latitude, longitude, location.latitude(), location.longitude())))
                .filter(candidate -> candidate.approxDistanceKm() <= radiusKm)
                .sorted(Comparator.comparingDouble(GeoCandidate::approxDistanceKm))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Cells of ring k have their centre at least k * 1.5 * edge away, and any point within
     * radiusKm sits in a cell whose centre is at most radiusKm + edge away, so
     * k >= (radiusKm + edge) / (1.5 * edge) guarantees full coverage.
     */
    private int ringsFor(double radiusKm) {
        double edgeKm = properties.getH3CellEdgeKm() > 0
                ? properties.getH3CellEdgeKm()
                : H3Resolution.averageEdgeKm(properties.getH3Resolution());

        int k = Math.max(1, (int) Math.ceil((radiusKm + edgeKm) / (1.5 * edgeKm)));
        return properties.getH3MaxK() > 0 ? Math.min(k, properties.getH3MaxK()) : k;
    }

    public String getCurrentCell(Long entityId) {
        Object value = redisTemplate.opsForValue().get(keys.getEntityCellKeyPrefix() + entityId);
        return value == null ? null : String.valueOf(value);
    }

    @Override
    public GeoLocation getLocation(Long entityId) {
        return locationStore.find(entityId);
    }
}