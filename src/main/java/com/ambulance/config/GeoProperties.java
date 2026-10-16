package com.ambulance.config;

import com.ambulance.geo.GeoStrategyType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ambulance.geo")
public class GeoProperties {

    /** Active search implementation. Switch with ambulance.geo.strategy. */
    private GeoStrategyType strategy = GeoStrategyType.H3_GRID_DISK;

    /** Write every location update into all indexes so the strategy can be switched without a rebuild. */
    private boolean dualWrite = true;

    /** Allow a single request to override the active strategy (useful for A/B and benchmarking). */
    private boolean allowRequestOverride = false;

    private int h3Resolution = 8;
    /** Override of the average H3 edge length in km. 0 = derive from resolution. */
    private double h3CellEdgeKm = 0.0;
    /** Safety cap for gridDisk k. 0 = uncapped. */
    private int h3MaxK = 0;

    private double defaultRadiusKm = 5.0;
    private double maxRadiusKm = 50.0;
    private int defaultLimit = 10;
    private int maxLimit = 50;
    private int candidateMultiplier = 5;
    private long staleLocationSeconds = 30;
    /** Live location is deleted from Redis after this age. */
    private long locationTtlSeconds = 120;
    /** How often the cleanup job runs. */
    private long staleCleanupMs = 60_000;
    private double maxAccuracyMeters = 100;


    public GeoStrategyType getStrategy() { return strategy; }
    public void setStrategy(GeoStrategyType strategy) { this.strategy = strategy; }
    public boolean isDualWrite() { return dualWrite; }
    public void setDualWrite(boolean dualWrite) { this.dualWrite = dualWrite; }
    public boolean isAllowRequestOverride() { return allowRequestOverride; }
    public void setAllowRequestOverride(boolean allowRequestOverride) { this.allowRequestOverride = allowRequestOverride; }
    public int getH3Resolution() { return h3Resolution; }
    public void setH3Resolution(int h3Resolution) { this.h3Resolution = h3Resolution; }
    public double getH3CellEdgeKm() { return h3CellEdgeKm; }
    public void setH3CellEdgeKm(double h3CellEdgeKm) { this.h3CellEdgeKm = h3CellEdgeKm; }
    public int getH3MaxK() { return h3MaxK; }
    public void setH3MaxK(int h3MaxK) { this.h3MaxK = h3MaxK; }
    public double getDefaultRadiusKm() { return defaultRadiusKm; }
    public void setDefaultRadiusKm(double defaultRadiusKm) { this.defaultRadiusKm = defaultRadiusKm; }
    public double getMaxRadiusKm() { return maxRadiusKm; }
    public void setMaxRadiusKm(double maxRadiusKm) { this.maxRadiusKm = maxRadiusKm; }
    public int getDefaultLimit() { return defaultLimit; }
    public void setDefaultLimit(int defaultLimit) { this.defaultLimit = defaultLimit; }
    public int getMaxLimit() { return maxLimit; }
    public void setMaxLimit(int maxLimit) { this.maxLimit = maxLimit; }
    public int getCandidateMultiplier() { return candidateMultiplier; }
    public void setCandidateMultiplier(int candidateMultiplier) { this.candidateMultiplier = candidateMultiplier; }
    public long getStaleLocationSeconds() { return staleLocationSeconds; }
    public void setStaleLocationSeconds(long staleLocationSeconds) { this.staleLocationSeconds = staleLocationSeconds; }
    public long getLocationTtlSeconds() { return locationTtlSeconds; }
    public void setLocationTtlSeconds(long value) { this.locationTtlSeconds = value; }
    public long getStaleCleanupMs() { return staleCleanupMs; }
    public void setStaleCleanupMs(long value) { this.staleCleanupMs = value; }
    public double getMaxAccuracyMeters() { return maxAccuracyMeters; }
    public void setMaxAccuracyMeters(double maxAccuracyMeters) { this.maxAccuracyMeters = maxAccuracyMeters; }
}