package com.ambulance.event;

import com.ambulance.geo.GeoLocation;

public interface LocationEventPublisher {
    void publishLocationUpdate(GeoLocation location);
}
