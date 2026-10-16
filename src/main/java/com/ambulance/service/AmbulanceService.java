package com.ambulance.service;

import com.ambulance.dto.*;
import com.ambulance.model.*;
import com.ambulance.repository.AmbulanceRepository;
import com.ambulance.repository.DriverRepository;
import com.ambulance.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final ServiceProviderRepository providerRepository;
    private final DriverRepository driverRepository;

    public AmbulanceService(AmbulanceRepository ambulanceRepository,
                            ServiceProviderRepository providerRepository,
                            DriverRepository driverRepository) {
        this.ambulanceRepository = ambulanceRepository;
        this.providerRepository = providerRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public Ambulance create(CreateAmbulanceRequest request) {
        if (ambulanceRepository.existsByVehicleNumber(request.vehicleNumber())) {
            throw new IllegalStateException("Vehicle number already exists: " + request.vehicleNumber());
        }
        if (ambulanceRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new IllegalStateException("Registration number already exists: " + request.registrationNumber());
        }

        Ambulance ambulance = new Ambulance();
        ambulance.setRegistrationNumber(request.registrationNumber());
        ambulance.setVehicleNumber(request.vehicleNumber());
        ambulance.setAmbulanceType(request.ambulanceType());
        ambulance.setVehicleModel(request.vehicleModel());
        ambulance.setCapacity(request.capacity());
        ambulance.setNurse(request.nurse());
        ambulance.setParamedic(request.paramedic());
        ambulance.setDoctor(request.doctor());
        ambulance.setStatus(AmbulanceStatus.OFFLINE);
        ambulance.setActive(true);

        if (request.providerId() != null) {
            ambulance.setProvider(providerRepository.findById(request.providerId())
                    .orElseThrow(() -> new IllegalStateException("Provider not found: " + request.providerId())));
        }

        return ambulanceRepository.save(ambulance);
    }

    @Transactional(readOnly = true)
    public AmbulanceResponse get(Long ambulanceId) {
        return toResponse(load(ambulanceId));
    }

    @Transactional
    public AmbulanceResponse updateStatus(Long ambulanceId, UpdateAmbulanceStatusRequest request) {
        Ambulance ambulance = load(ambulanceId);
        ambulance.setStatus(request.status());
        return toResponse(ambulance);
    }

    public Ambulance load(Long ambulanceId) {
        return ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new AmbulanceNotFoundException(ambulanceId));
    }

    public AmbulanceResponse toResponse(Ambulance a) {
        Driver driver = driverRepository.findByAmbulanceId(a.getId()).orElse(null);
        return new AmbulanceResponse(
                a.getId(), a.getRegistrationNumber(), a.getVehicleNumber(), a.getAmbulanceType(),
                a.getVehicleModel(), a.getCapacity(),
                a.getProvider() == null ? null : a.getProvider().getId(),
                a.isNurse(), a.isParamedic(), a.isDoctor(), a.getStatus(), a.isActive(),
                driver == null ? null : driver.getId(),
                a.getDriverName(), a.getDriverContact());
    }
}