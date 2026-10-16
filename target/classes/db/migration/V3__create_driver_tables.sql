CREATE TABLE driver (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        full_name VARCHAR(150) NOT NULL,
                        email VARCHAR(150) NOT NULL UNIQUE,
                        phone VARCHAR(20) NOT NULL UNIQUE,
                        password_hash VARCHAR(255) NOT NULL,
                        license_number VARCHAR(50) NOT NULL UNIQUE,
                        license_expiry DATE,
                        ambulance_id BIGINT UNIQUE,
                        active BOOLEAN NOT NULL DEFAULT TRUE,
                        last_login_at TIMESTAMP NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        CONSTRAINT fk_driver_ambulance FOREIGN KEY (ambulance_id) REFERENCES ambulance(id)
);

CREATE TABLE driver_refresh_token (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      driver_id BIGINT NOT NULL,
                                      token_hash VARCHAR(255) NOT NULL UNIQUE,
                                      expires_at TIMESTAMP NOT NULL,
                                      revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_refresh_driver FOREIGN KEY (driver_id) REFERENCES driver(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_driver ON driver_refresh_token(driver_id, revoked);