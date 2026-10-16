# Ambulance Nearby + Live Location Service

Spring Boot 3 + Java 21 + MySQL + Redis + H3.

## Responsibilities

- MySQL: ambulance master data, driver data, status, capabilities.
- Redis: current location, current H3 cell, H3 cell -> ambulance IDs.
- H3: spatial partitioning and candidate lookup.
- Java Haversine: exact radius/distance calculation.
- Redis Pub/Sub: lightweight location-update notification.
- Flyway: database schema migration.

## SOLID structure

- `GeoIndexStrategy`: abstraction for spatial indexing.
- `H3GeoIndexStrategy`: H3 implementation.
- `DistanceCalculator`: exact distance abstraction.
- `EtaCalculator`: ETA abstraction.
- `AmbulanceRepository`: persistence abstraction.
- `LocationEventPublisher`: event abstraction.
- `NearbyAmbulanceService`: orchestration only.
- `AmbulanceLocationService`: location-update orchestration only.

A future implementation can replace H3, ETA, Redis Pub/Sub or MySQL without changing the application service contracts.

## Run

```bash
docker compose up -d
mvn spring-boot:run
```

## APIs

### Update ambulance location

```http
PUT /api/v1/ambulances/{ambulanceId}/location
Content-Type: application/json
```

```json
{
  "latitude": 12.9716,
  "longitude": 77.5946,
  "speedKmph": 34.5,
  "heading": 125.0,
  "accuracyMeters": 7.5
}
```

### Nearby ambulances

```http
POST /api/v1/ambulances/nearby
Content-Type: application/json
```

```json
{
  "latitude": 12.9716,
  "longitude": 77.5946,
  "radiusKm": 5,
  "limit": 10,
  "requiredType": "ADVANCED",
  "requireDoctor": false,
  "requireNurse": false,
  "requireParamedic": true
}
```

## Important production note

The code intentionally separates live location from master data. Do not update MySQL for every GPS packet. GPS packets should update Redis; MySQL should remain the source of relatively stable ambulance/master/status information.

For production live tracking, add authentication, heartbeat/TTL cleanup, Kafka/Redis Streams for durable events, WebSocket/SSE for user-map updates, routing-provider ETA, rate limiting, metrics/tracing, and atomic Redis location moves.
