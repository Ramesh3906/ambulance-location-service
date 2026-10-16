package com.ambulance.controller;

import com.ambulance.dto.*;
import com.ambulance.service.TripService;
import com.ambulance.trip.TripTrackingService;
import com.ambulance.trip.TripTrackingStream;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private static final long STREAM_TIMEOUT_MILLIS = 30 * 60 * 1000L;

    private final TripService tripService;
    private final TripTrackingService trackingService;
    private final TripTrackingStream trackingStream;

    public TripController(TripService tripService,
                          TripTrackingService trackingService,
                          TripTrackingStream trackingStream) {
        this.tripService = tripService;
        this.trackingService = trackingService;
        this.trackingStream = trackingStream;
    }

    @PostMapping
    public ResponseEntity<TripResponse> create(@Valid @RequestBody CreateTripRequest request) {
        return ResponseEntity.ok(tripService.create(request));
    }

    @PostMapping("/{tripId}/accept")
    public ResponseEntity<TripResponse> accept(@PathVariable Long tripId,
                                               @Valid @RequestBody AcceptTripRequest request) {
        return ResponseEntity.ok(tripService.accept(tripId, request));
    }

    @PostMapping("/{tripId}/arrived")
    public ResponseEntity<TripResponse> arrived(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.markArrived(tripId));
    }

    @PostMapping("/{tripId}/start")
    public ResponseEntity<TripResponse> start(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.start(tripId));
    }

    @PostMapping("/{tripId}/complete")
    public ResponseEntity<TripResponse> complete(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.complete(tripId));
    }

    @PostMapping("/{tripId}/cancel")
    public ResponseEntity<TripResponse> cancel(@PathVariable Long tripId,
                                               @RequestBody(required = false) CancelTripRequest request) {
        return ResponseEntity.ok(tripService.cancel(tripId, request));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> get(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.get(tripId));
    }

    /** Polling endpoint: ETA + remaining distance to the patient. */
    @GetMapping("/{tripId}/tracking")
    public ResponseEntity<TripTrackingResponse> tracking(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.tracking(tripId));
    }

    /** Push endpoint: emits a new ETA on every driver GPS packet. */
    @GetMapping("/{tripId}/tracking/stream")
    public SseEmitter stream(@PathVariable Long tripId) {
        SseEmitter emitter = trackingStream.subscribe(tripId, STREAM_TIMEOUT_MILLIS);
        TripTrackingResponse last = trackingService.lastSnapshot(tripId);
        if (last != null) {
            trackingStream.send(last);
        }
        return emitter;
    }
}