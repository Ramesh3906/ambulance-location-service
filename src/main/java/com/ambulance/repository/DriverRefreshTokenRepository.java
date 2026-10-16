package com.ambulance.repository;

import com.ambulance.model.DriverRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DriverRefreshTokenRepository extends JpaRepository<DriverRefreshToken, Long> {

    Optional<DriverRefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update DriverRefreshToken t set t.revoked = true where t.driver.id = :driverId and t.revoked = false")
    void revokeAllForDriver(Long driverId);
}