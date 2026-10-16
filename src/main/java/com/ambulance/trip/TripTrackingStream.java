package com.ambulance.trip;

import com.ambulance.dto.TripTrackingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class TripTrackingStream {

    private final Map<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public TripTrackingStream(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SseEmitter subscribe(Long tripId, long timeoutMillis) {
        SseEmitter emitter = new SseEmitter(timeoutMillis);
        Set<SseEmitter> tripEmitters = emitters.computeIfAbsent(tripId, id -> new CopyOnWriteArraySet<>());
        tripEmitters.add(emitter);

        emitter.onCompletion(() -> remove(tripId, emitter));
        emitter.onTimeout(() -> remove(tripId, emitter));
        emitter.onError(error -> remove(tripId, emitter));
        return emitter;
    }

    public void send(TripTrackingResponse tracking) {
        Set<SseEmitter> tripEmitters = emitters.get(tracking.tripId());
        if (tripEmitters == null) {
            return;
        }
        for (SseEmitter emitter : tripEmitters) {
            try {
                emitter.send(SseEmitter.event().name("tracking").data(tracking));
            } catch (IOException | IllegalStateException e) {
                remove(tracking.tripId(), emitter);
            }
        }
    }

    public void broadcast(String payload) {
        try {
            send(objectMapper.readValue(payload, TripTrackingResponse.class));
        } catch (IOException e) {
            // Malformed payloads must never kill the Redis listener thread.
        }
    }

    private void remove(Long tripId, SseEmitter emitter) {
        Set<SseEmitter> tripEmitters = emitters.get(tripId);
        if (tripEmitters != null) {
            tripEmitters.remove(emitter);
            if (tripEmitters.isEmpty()) {
                emitters.remove(tripId);
            }
        }
    }
}