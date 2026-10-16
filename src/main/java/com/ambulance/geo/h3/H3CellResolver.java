package com.ambulance.geo.h3;

import com.ambulance.config.GeoProperties;
import com.ambulance.geo.GeoCellResolver;
import com.uber.h3core.H3Core;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class H3CellResolver implements GeoCellResolver {

    private final H3Core h3;
    private final GeoProperties properties;

    public H3CellResolver(GeoProperties properties) throws IOException {
        this.h3 = H3Core.newInstance();
        this.properties = properties;
    }

    @Override
    public String cellFor(double latitude, double longitude) {
        return Long.toUnsignedString(h3.latLngToCell(latitude, longitude, properties.getH3Resolution()));
    }
}