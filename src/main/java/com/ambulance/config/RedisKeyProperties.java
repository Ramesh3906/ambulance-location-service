package com.ambulance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ambulance.redis")
public class RedisKeyProperties {

    private String locationKeyPrefix = "ambulance:location:";
    private String cellKeyPrefix = "ambulance:h3:cell:";
    private String entityCellKeyPrefix = "ambulance:h3:entity-cell:";
    private String geoKey = "ambulance:geo:index";
    private String locationChannel = "ambulance:location-updates";

    public String getLocationKeyPrefix() { return locationKeyPrefix; }
    public void setLocationKeyPrefix(String locationKeyPrefix) { this.locationKeyPrefix = locationKeyPrefix; }
    public String getCellKeyPrefix() { return cellKeyPrefix; }
    public void setCellKeyPrefix(String cellKeyPrefix) { this.cellKeyPrefix = cellKeyPrefix; }
    public String getEntityCellKeyPrefix() { return entityCellKeyPrefix; }
    public void setEntityCellKeyPrefix(String entityCellKeyPrefix) { this.entityCellKeyPrefix = entityCellKeyPrefix; }
    public String getGeoKey() { return geoKey; }
    public void setGeoKey(String geoKey) { this.geoKey = geoKey; }
    public String getLocationChannel() { return locationChannel; }
    public void setLocationChannel(String locationChannel) { this.locationChannel = locationChannel; }

    private String tripSessionKeyPrefix = "ambulance:trip:session:";
    private String tripSnapshotKeyPrefix = "ambulance:trip:tracking:";
    private String tripChannel = "ambulance:trip-updates";

    public String getTripSessionKeyPrefix() { return tripSessionKeyPrefix; }
    public void setTripSessionKeyPrefix(String tripSessionKeyPrefix) { this.tripSessionKeyPrefix = tripSessionKeyPrefix; }
    public String getTripSnapshotKeyPrefix() { return tripSnapshotKeyPrefix; }
    public void setTripSnapshotKeyPrefix(String tripSnapshotKeyPrefix) { this.tripSnapshotKeyPrefix = tripSnapshotKeyPrefix; }
    public String getTripChannel() { return tripChannel; }
    public void setTripChannel(String tripChannel) { this.tripChannel = tripChannel; }
}