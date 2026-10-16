package com.ambulance.geo.redis;

import com.ambulance.config.RedisKeyProperties;
import com.ambulance.geo.GeoLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisGeoLocationStore {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisKeyProperties keys;

    public RedisGeoLocationStore(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 RedisKeyProperties keys) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.keys = keys;
    }

    public void save(Long entityId, GeoLocation location) {
        try {
            redisTemplate.opsForValue()
                    .set(keys.getLocationKeyPrefix() + entityId, objectMapper.writeValueAsString(location));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize ambulance location: " + entityId, e);
        }
    }

    public GeoLocation find(Long entityId) {
        Object value = redisTemplate.opsForValue().get(keys.getLocationKeyPrefix() + entityId);
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.readValue(String.valueOf(value), GeoLocation.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid location data for ambulance: " + entityId, e);
        }
    }

    public void delete(Long entityId) {
        redisTemplate.delete(keys.getLocationKeyPrefix() + entityId);
    }
}