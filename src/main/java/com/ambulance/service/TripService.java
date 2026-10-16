package com.ambulance.service;

import com.ambulance.dto.*;
import com.ambulance.model.*;
import com.ambulance.repository.AmbulanceRepository;
import com.ambulance.repository.TripRepository;
import com.ambulance.trip.TripTrackingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final TripTrackingService trackingService;

    public TripService(TripRepository tripRepository,
                       AmbulanceRepository ambulanceRepository,
                       TripTrackingService trackingService) {
        this.tripRepository = tripRepository;
        this.ambulanceRepository = ambulanceRepository;
        this.trackingService = trackingService;
    }

    @Transactional
    public TripResponse create(CreateTripRequest request) {
        Trip trip = new Trip();
        trip.setReference("TRIP-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        trip.setPickupLatitude(request.pickupLatitude());
        trip.setPickupLongitude(request.pickupLongitude());
        trip.setPickupAddress(request.pickupAddress());
        trip.setDropLatitude(request.dropLatitude());
        trip.setDropLongitude(request.dropLongitude());
        trip.setDropAddress(request.dropAddress());
        trip.setPatientName(request.patientName());
        trip.setPatientContact(request.patientContact());
        trip.setRequiredType(request.requiredType());
        trip.setStatus(TripStatus.REQUESTED);

        return toResponse(tripRepository.save(trip));
    }

    /** Driver accepts -> ambulance becomes EN_ROUTE and live tracking starts immediately. */
    @Transactional
    public TripResponse accept(Long tripId, AcceptTripRequest request) {
        Trip trip = load(tripId);
        requireStatus(trip, TripStatus.REQUESTED);

        Ambulance ambulance = ambulanceRepository.findById(request.ambulanceId())
                .orElseThrow(() -> new AmbulanceNotFoundException(request.ambulanceId()));

        if (!ambulance.isActive()) {
            throw new IllegalStateException("Ambulance is inactive: " + ambulance.getId());
        }
        if (ambulance.getStatus() != AmbulanceStatus.AVAILABLE) {
            throw new IllegalStateException("Ambulance is not available: " + ambulance.getId());
        }
        tripRepository.findFirstByAmbulanceIdAndStatusIn(ambulance.getId(), TripStatus.ACTIVE)
                .ifPresent(active -> {
                    throw new IllegalStateException("Ambulance already on trip: " + active.getReference());
                });

        trip.setAmbulance(ambulance);
        trip.setStatus(TripStatus.EN_ROUTE_TO_PICKUP);
        trip.setAcceptedAt(Instant.now());
        ambulance.setStatus(AmbulanceStatus.EN_ROUTE);

        trackingService.startTracking(trip);
        return toResponse(trip);
    }

    @Transactional
    public TripResponse markArrived(Long tripId) {
        Trip trip = load(tripId);
        requireStatus(trip, TripStatus.EN_ROUTE_TO_PICKUP);

        trip.setStatus(TripStatus.ARRIVED_AT_PICKUP);
        trip.setArrivedAt(Instant.now());
        snapshotEta(trip);
        trackingService.refreshTracking(trip);
        return toResponse(trip);
    }

    /** Patient on board: the tracking target switches from pickup to drop. */
    @Transactional
    public TripResponse start(Long tripId) {
        Trip trip = load(tripId);
        requireStatus(trip, TripStatus.ARRIVED_AT_PICKUP);

        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setStartedAt(Instant.now());
        trip.getAmbulance().setStatus(AmbulanceStatus.BUSY);
        trackingService.refreshTracking(trip);
        return toResponse(trip);
    }

    @Transactional
    public TripResponse complete(Long tripId) {
        Trip trip = load(tripId);
        if (trip.getStatus() != TripStatus.IN_PROGRESS && trip.getStatus() != TripStatus.ARRIVED_AT_PICKUP) {
            throw new IllegalStateException("Trip cannot be completed from status " + trip.getStatus());
        }

        snapshotEta(trip);
        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(Instant.now());
        releaseAmbulance(trip);
        return toResponse(trip);
    }

    @Transactional
    public TripResponse cancel(Long tripId, CancelTripRequest request) {
        Trip trip = load(tripId);
        if (trip.getStatus().isTerminal()) {
            throw new IllegalStateException("Trip already " + trip.getStatus());
        }

        trip.setStatus(TripStatus.CANCELLED);
        trip.setCancelledAt(Instant.now());
        trip.setCancelReason(request == null ? null : request.reason());
        releaseAmbulance(trip);
        return toResponse(trip);
    }

    @Transactional(readOnly = true)
    public TripResponse get(Long tripId) {
        return toResponse(load(tripId));
    }

    /** Live ETA + remaining distance to the patient (or drop point once on board). */
    @Transactional(readOnly = true)
    public TripTrackingResponse tracking(Long tripId) {
        Trip trip = load(tripId);

        if (trip.getStatus() == TripStatus.REQUESTED) {
            throw new IllegalStateException("Trip is not accepted yet: " + trip.getReference());
        }
        if (trip.getStatus().isTerminal()) {
            throw new IllegalStateException("Trip already " + trip.getStatus());
        }

        TripTrackingResponse tracking = trackingService.currentTracking(trip);
        if (tracking == null) {
            throw new IllegalStateException("Live location not available for trip " + trip.getReference());
        }
        return tracking;
    }

    private void releaseAmbulance(Trip trip) {
        trackingService.stopTracking(trip);
        Ambulance ambulance = trip.getAmbulance();
        if (ambulance != null) {
            ambulance.setStatus(AmbulanceStatus.AVAILABLE);
        }
    }

    private void snapshotEta(Trip trip) {
        TripTrackingResponse tracking = trackingService.currentTracking(trip);
        if (tracking != null) {
            trip.setLastRemainingKm(tracking.remainingDistanceKm());
            trip.setLastEtaMinutes(tracking.etaMinutes());
        }
    }

    private Trip load(Long tripId) {
        return tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
    }

    private void requireStatus(Trip trip, TripStatus expected) {
        if (trip.getStatus() != expected) {
            throw new IllegalStateException(
                    "Trip " + trip.getReference() + " is " + trip.getStatus() + ", expected " + expected);
        }
    }

    private TripResponse toResponse(Trip trip) {
        Ambulance ambulance = trip.getAmbulance();
        return new TripResponse(
                trip.getId(),
                trip.getReference(),
                trip.getStatus(),
                ambulance == null ? null : ambulance.getId(),
                ambulance == null ? null : ambulance.getVehicleNumber(),
                ambulance == null ? null : ambulance.getDriverName(),
                ambulance == null ? null : ambulance.getDriverContact(),
                trip.getPickupLatitude(),
                trip.getPickupLongitude(),
                trip.getDropLatitude(),
                trip.getDropLongitude(),
                trip.getAcceptedAt(),
                trip.getArrivedAt(),
                trip.getStartedAt(),
                trip.getCompletedAt(),
                trip.getCancelledAt(),
                trip.getLastRemainingKm(),
                trip.getLastEtaMinutes(),
                "/api/v1/trips/" + trip.getId() + "/tracking",
                "/api/v1/trips/" + trip.getId() + "/tracking/stream");
    }
}