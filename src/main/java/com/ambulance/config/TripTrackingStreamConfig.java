package com.ambulance.config;

import com.ambulance.trip.TripTrackingStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;

@Configuration
public class TripTrackingStreamConfig {

    @Bean
    public RedisMessageListenerContainer tripTrackingListenerContainer(
            RedisConnectionFactory connectionFactory,
            TripTrackingStream stream,
            RedisKeyProperties keys) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
                (message, pattern) -> stream.broadcast(new String(message.getBody(), StandardCharsets.UTF_8)),
                new ChannelTopic(keys.getTripChannel()));
        return container;
    }
}