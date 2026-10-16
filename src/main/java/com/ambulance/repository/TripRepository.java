package com.ambulance.repository;

import com.ambulance.model.Trip;
import com.ambulance.model.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Optional<Trip> findByReference(String reference);

    Optional<Trip> findFirstByAmbulanceIdAndStatusIn(Long ambulanceId, Collection<TripStatus> statuses);
}