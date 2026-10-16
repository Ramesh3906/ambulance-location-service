package com.ambulance.service;

import com.ambulance.dto.*;
import com.ambulance.model.Ambulance;
import com.ambulance.model.AmbulanceStatus;
import com.ambulance.model.Driver;
import com.ambulance.model.DriverRefreshToken;
import com.ambulance.repository.AmbulanceRepository;
import com.ambulance.repository.DriverRefreshTokenRepository;
import com.ambulance.repository.DriverRepository;
import com.ambulance.security.JwtService;
import com.ambulance.security.InvalidTokenException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DriverAuthService {

    private final DriverRepository driverRepository;
    private final DriverRefreshTokenRepository refreshTokenRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final AmbulanceService ambulanceService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public DriverAuthService(DriverRepository driverRepository,
                             DriverRefreshTokenRepository refreshTokenRepository,
                             AmbulanceRepository ambulanceRepository,
                             AmbulanceService ambulanceService,
                             PasswordEncoder passwordEncoder,
                             JwtService jwtService) {
        this.driverRepository = driverRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.ambulanceRepository = ambulanceRepository;
        this.ambulanceService = ambulanceService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(DriverRegistrationRequest request) {
        if (driverRepository.existsByEmail(request.email())) {
            throw new DriverAuthException("Email already registered: " + request.email());
        }
        if (driverRepository.existsByPhone(request.phone())) {
            throw new DriverAuthException("Phone already registered: " + request.phone());
        }
        if (driverRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new DriverAuthException("License already registered: " + request.licenseNumber());
        }

        Driver driver = new Driver();
        driver.setFullName(request.fullName());
        driver.setEmail(request.email().toLowerCase());
        driver.setPhone(request.phone());
        driver.setPasswordHash(passwordEncoder.encode(request.password()));
        driver.setLicenseNumber(request.licenseNumber());
        driver.setLicenseExpiry(request.licenseExpiry());
        driver.setActive(true);

        if (request.ambulanceId() != null) {
            driver.setAmbulance(resolveFreeAmbulance(request.ambulanceId()));
        }

        driverRepository.save(driver);
        syncDriverDetailsToAmbulance(driver);

        return issueTokens(driver);
    }

    @Transactional
    public AuthResponse login(DriverLoginRequest request) {
        Driver driver = driverRepository.findByEmail(request.username().toLowerCase())
                .or(() -> driverRepository.findByPhone(request.username()))
                // Same message for unknown user and wrong password to avoid account enumeration.
                .orElseThrow(() -> new DriverAuthException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), driver.getPasswordHash())) {
            throw new DriverAuthException("Invalid credentials");
        }
        if (!driver.isActive()) {
            throw new DriverAuthException("Driver account is disabled");
        }

        driver.setLastLoginAt(Instant.now());

        // One active session per driver.
        refreshTokenRepository.revokeAllForDriver(driver.getId());

        return issueTokens(driver);
    }

    /**
     * Rotates the refresh token: the presented one is revoked and a new pair is returned.
     */
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String hash = jwtService.hashRefreshToken(request.refreshToken());

        DriverRefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not recognised"));

        if (!stored.isUsable()) {
            // A revoked token being replayed means it may have leaked; drop every session.
            refreshTokenRepository.revokeAllForDriver(stored.getDriver().getId());
            throw new InvalidTokenException("Refresh token expired or already used");
        }

        Driver driver = stored.getDriver();
        if (!driver.isActive()) {
            throw new DriverAuthException("Driver account is disabled");
        }

        stored.setRevoked(true);
        return issueTokens(driver);
    }

    @Transactional
    public void logout(Long driverId) {
        refreshTokenRepository.revokeAllForDriver(driverId);
    }

    @Transactional(readOnly = true)
    public DriverProfileResponse profile(Long driverId) {
        return toProfile(load(driverId));
    }

    @Transactional
    public DriverProfileResponse assignAmbulance(Long driverId, AssignAmbulanceRequest request) {
        Driver driver = load(driverId);

        if (driver.getAmbulance() != null) {
            throw new DriverAuthException("Driver already has an ambulance assigned");
        }

        Ambulance ambulance = request.ambulanceId() != null
                ? resolveFreeAmbulance(request.ambulanceId())
                : ambulanceService.create(requireDetails(request));

        driver.setAmbulance(ambulance);
        syncDriverDetailsToAmbulance(driver);
        ambulance.setStatus(AmbulanceStatus.AVAILABLE);
        return toProfile(driver);
    }


    @Transactional
    public DriverProfileResponse unassignAmbulance(Long driverId) {
        Driver driver = load(driverId);
        driver.setAmbulance(null);
        return toProfile(driver);
    }

    private CreateAmbulanceRequest requireDetails(AssignAmbulanceRequest request) {
        if (request.ambulance() == null) {
            throw new DriverAuthException("Provide either ambulanceId or full ambulance details");
        }
        return request.ambulance();
    }

    private AuthResponse issueTokens(Driver driver) {
        Ambulance ambulance = driver.getAmbulance();
        Long ambulanceId = ambulance == null ? null : ambulance.getId();

        String accessToken = jwtService.generateAccessToken(driver.getId(), driver.getEmail(), ambulanceId);

        String refreshToken = jwtService.generateRefreshToken();
        DriverRefreshToken entity = new DriverRefreshToken();
        entity.setDriver(driver);
        entity.setTokenHash(jwtService.hashRefreshToken(refreshToken));
        entity.setExpiresAt(jwtService.refreshTokenExpiry());
        refreshTokenRepository.save(entity);

        return new AuthResponse(
                driver.getId(),
                driver.getFullName(),
                ambulanceId,
                ambulance == null ? null : ambulance.getVehicleNumber(),
                ambulanceId != null,
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.accessTokenSeconds());
    }

    private Ambulance resolveFreeAmbulance(Long ambulanceId) {
        Ambulance ambulance = ambulanceRepository.findById(ambulanceId)
                .orElseThrow(() -> new AmbulanceNotFoundException(ambulanceId));

        driverRepository.findByAmbulanceId(ambulanceId).ifPresent(existing -> {
            throw new DriverAuthException("Ambulance already assigned to driver " + existing.getId());
        });

        return ambulance;
    }

    /**
     * Keeps the legacy denormalised driver columns on ambulance in sync.
     */
    private void syncDriverDetailsToAmbulance(Driver driver) {
        Ambulance ambulance = driver.getAmbulance();
        if (ambulance != null) {
            ambulance.setDriverName(driver.getFullName());
            ambulance.setDriverContact(driver.getPhone());
        }
    }

    private Driver load(Long driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
    }

    private DriverProfileResponse toProfile(Driver driver) {
        Ambulance ambulance = driver.getAmbulance();
        return new DriverProfileResponse(
                driver.getId(),
                driver.getFullName(),
                driver.getEmail(),
                driver.getPhone(),
                driver.getLicenseNumber(),
                driver.getLicenseExpiry(),
                driver.isActive(),
                driver.getLastLoginAt(),
                ambulance == null ? null : ambulance.getId(),
                ambulance == null ? null : ambulance.getVehicleNumber(),
                ambulance == null ? null : ambulance.getRegistrationNumber(),
                ambulance == null ? null : ambulance.getAmbulanceType(),
                ambulance == null ? null : ambulance.getStatus());
    }
}