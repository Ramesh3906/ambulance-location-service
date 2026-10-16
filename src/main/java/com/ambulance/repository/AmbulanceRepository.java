package com.ambulance.repository;

import com.ambulance.model.Ambulance;
import com.ambulance.model.AmbulanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
    List<Ambulance> findByIdInAndActiveTrueAndStatus(
            Collection<Long> ids,
            AmbulanceStatus status
    );
    boolean existsByVehicleNumber(String vehicleNumber);
    boolean existsByRegistrationNumber(String registrationNumber);
}
