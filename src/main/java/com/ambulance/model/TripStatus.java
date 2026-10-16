package com.ambulance.model;

import java.util.EnumSet;
import java.util.Set;

public enum TripStatus {
    REQUESTED,
    EN_ROUTE_TO_PICKUP,
    ARRIVED_AT_PICKUP,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public static final Set<TripStatus> ACTIVE = EnumSet.of(EN_ROUTE_TO_PICKUP, ARRIVED_AT_PICKUP, IN_PROGRESS);

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
}