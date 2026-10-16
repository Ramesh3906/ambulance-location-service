CREATE TABLE trip (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      reference VARCHAR(40) NOT NULL UNIQUE,
                      patient_name VARCHAR(150),
                      patient_contact VARCHAR(30),
                      pickup_latitude DOUBLE NOT NULL,
                      pickup_longitude DOUBLE NOT NULL,
                      pickup_address VARCHAR(255),
                      drop_latitude DOUBLE,
                      drop_longitude DOUBLE,
                      drop_address VARCHAR(255),
                      required_type VARCHAR(30),
                      ambulance_id BIGINT,
                      status VARCHAR(30) NOT NULL,
                      accepted_at TIMESTAMP NULL,
                      arrived_at TIMESTAMP NULL,
                      started_at TIMESTAMP NULL,
                      completed_at TIMESTAMP NULL,
                      cancelled_at TIMESTAMP NULL,
                      cancel_reason VARCHAR(255),
                      last_remaining_km DOUBLE,
                      last_eta_minutes BIGINT,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      CONSTRAINT fk_trip_ambulance FOREIGN KEY (ambulance_id) REFERENCES ambulance(id)
);

CREATE INDEX idx_trip_ambulance_status ON trip(ambulance_id, status);
CREATE INDEX idx_trip_status ON trip(status);