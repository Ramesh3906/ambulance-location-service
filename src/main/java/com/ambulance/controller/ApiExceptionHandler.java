package com.ambulance.controller;

import com.ambulance.security.InvalidTokenException;
import com.ambulance.service.AmbulanceNotFoundException;
import com.ambulance.service.DriverAuthException;
import com.ambulance.service.DriverNotFoundException;
import com.ambulance.service.TripNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AmbulanceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> ambulanceNotFound(AmbulanceNotFoundException ex) {
        return Map.of(
                "code", "AMBULANCE_NOT_FOUND",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> illegalState(IllegalStateException ex) {
        return Map.of(
                "code", "INVALID_OPERATION",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(TripNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> tripNotFound(TripNotFoundException ex) {
        return Map.of("code", "TRIP_NOT_FOUND", "message", ex.getMessage());
    }

    @ExceptionHandler(DriverNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> driverNotFound(DriverNotFoundException ex) {
        return Map.of("code", "DRIVER_NOT_FOUND", "message", ex.getMessage());
    }

    @ExceptionHandler(DriverAuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> driverAuth(DriverAuthException ex) {
        return Map.of("code", "AUTH_FAILED", "message", ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> invalidToken(InvalidTokenException ex) {
        return Map.of("code", "INVALID_TOKEN", "message", ex.getMessage());
    }
}
