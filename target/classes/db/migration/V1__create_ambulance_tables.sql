CREATE TABLE service_provider (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    contact_number VARCHAR(30),
    email VARCHAR(150),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    emergency_service_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE ambulance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    vehicle_number VARCHAR(50) NOT NULL UNIQUE,
    ambulance_type VARCHAR(30) NOT NULL,
    vehicle_model VARCHAR(100),
    capacity INT,
    provider_id BIGINT,
    has_nurse BOOLEAN NOT NULL DEFAULT FALSE,
    has_paramedic BOOLEAN NOT NULL DEFAULT FALSE,
    has_doctor BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    driver_name VARCHAR(150),
    driver_contact VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ambulance_provider
        FOREIGN KEY (provider_id) REFERENCES service_provider(id)
);

CREATE INDEX idx_ambulance_search
    ON ambulance(status, active, ambulance_type);

CREATE INDEX idx_ambulance_provider
    ON ambulance(provider_id);
