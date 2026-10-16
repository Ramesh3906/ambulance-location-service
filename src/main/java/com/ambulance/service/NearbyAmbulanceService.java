package com.ambulance.service;

import com.ambulance.config.GeoProperties;
import com.ambulance.dto.NearbyAmbulanceDto;
import com.ambulance.dto.NearbySearchRequest;
import com.ambulance.eta.EtaCalculator;
import com.ambulance.eta.EtaResult;
import com.ambulance.geo.*;
import com.ambulance.model.Ambulance;
import com.ambulance.model.AmbulanceStatus;
import com.ambulance.repository.AmbulanceRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class NearbyAmbulanceService {

    private final GeoIndexStrategySelector strategySelector;
    private final AmbulanceRepository ambulanceRepository;
    private final EtaCalculator etaCalculator;
    private final GeoProperties properties;

    public NearbyAmbulanceService(GeoIndexStrategySelector strategySelector,
                                  AmbulanceRepository ambulanceRepository,
                                  EtaCalculator etaCalculator,
                                  GeoProperties properties) {
        this.strategySelector = strategySelector;
        this.ambulanceRepository = ambulanceRepository;
        this.etaCalculator = etaCalculator;
        this.properties = properties;
    }

    public List<NearbyAmbulanceDto> findNearby(NearbySearchRequest request) {
        int limit = Math.min(
                request.limit() == null ? properties.getDefaultLimit() : request.limit(),
                properties.getMaxLimit());

        double radiusKm = Math.min(
                request.radiusKm() == null ? properties.getDefaultRadiusKm() : request.radiusKm(),
                properties.getMaxRadiusKm());

        GeoIndexStrategy strategy = strategySelector.select(request.strategy());

        List<GeoCandidate> candidates = strategy.findWithinRadius(
                request.latitude(),
                request.longitude(),
                radiusKm,
                limit * properties.getCandidateMultiplier());

        if (candidates.isEmpty()) {
            return List.of();
        }

        Set<Long> ids = candidates.stream()
                .map(GeoCandidate::entityId)
                .collect(Collectors.toSet());

        Map<Long, Ambulance> available = ambulanceRepository
                .findByIdInAndActiveTrueAndStatus(ids, AmbulanceStatus.AVAILABLE)
                .stream()
                .filter(capabilityPredicate(request))
                .collect(Collectors.toMap(Ambulance::getId, a -> a));

        return candidates.stream()
                .filter(candidate -> available.containsKey(candidate.entityId()))
                .map(candidate -> toDto(
                        strategy,
                        candidate,
                        available.get(candidate.entityId()),
                        request.latitude(),
                        request.longitude()))
                .filter(Objects::nonNull)
                .limit(limit)
                .toList();
    }

    private Predicate<Ambulance> capabilityPredicate(NearbySearchRequest request) {
        return ambulance ->
                (request.requiredType() == null || ambulance.getAmbulanceType() == request.requiredType())
                        && (!Boolean.TRUE.equals(request.requireDoctor()) || ambulance.isDoctor())
                        && (!Boolean.TRUE.equals(request.requireNurse()) || ambulance.isNurse())
                        && (!Boolean.TRUE.equals(request.requireParamedic()) || ambulance.isParamedic());
    }

    private NearbyAmbulanceDto toDto(GeoIndexStrategy strategy,
                                     GeoCandidate candidate,
                                     Ambulance ambulance,
                                     double userLat,
                                     double userLng) {
        GeoLocation location = strategy.getLocation(ambulance.getId());
        if (location == null) {
            return null;
        }

        if (System.currentTimeMillis() - location.updatedAt() > properties.getStaleLocationSeconds() * 1000L) {
            return null;
        }

        EtaResult eta = etaCalculator.calculate(
                location.latitude(), location.longitude(), userLat, userLng, location.speedKmph());

        return new NearbyAmbulanceDto(
                ambulance.getId(),
                ambulance.getVehicleNumber(),
                ambulance.getAmbulanceType(),
                ambulance.isNurse(),
                ambulance.isParamedic(),
                ambulance.isDoctor(),
                candidate.approxDistanceKm(),
                eta.etaMinutes(),
                location.latitude(),
                location.longitude(),
                location.speedKmph(),
                location.heading(),
                location.accuracyMeters(),
                ambulance.getDriverName(),
                ambulance.getDriverContact());
    }
}