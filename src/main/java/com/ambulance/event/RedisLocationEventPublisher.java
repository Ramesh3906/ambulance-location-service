package com.ambulance.event;

import com.ambulance.geo.GeoLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisLocationEventPublisher implements LocationEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String channel;

    public RedisLocationEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${ambulance.redis.location-channel:ambulance:location-updates}") String channel
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.channel = channel;
    }

    @Override
    public void publishLocationUpdate(GeoLocation location) {
        try {
            redisTemplate.convertAndSend(channel, objectMapper.writeValueAsString(location));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to publish location event", e);
        }
    }
}
