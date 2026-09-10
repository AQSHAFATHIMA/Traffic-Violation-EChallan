package com.example.traffic;

import java.time.LocalDateTime;

public class Violation {

    private final String violationId;
    private final String vehicleNumber;
    private final ViolationType violationType;
    private final ViolationSeverity severity;
    private final double speed;
    private final double speedLimit;
    private final String location;
    private final LocalDateTime timestamp;

    public Violation(
            String violationId,
            String vehicleNumber,
            ViolationType violationType,
            ViolationSeverity severity,
            double speed,
            double speedLimit,
            String location) {

        if (violationId == null
                || violationId.isBlank()) {
            throw new IllegalArgumentException(
                    "Violation ID cannot be empty");
        }

        if (vehicleNumber == null
                || vehicleNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Vehicle number cannot be empty");
        }

        if (violationType == null) {
            throw new IllegalArgumentException(
                    "Violation type is required");
        }

        if (severity == null) {
            throw new IllegalArgumentException(
                    "Violation severity is required");
        }

        if (speed < 0) {
            throw new IllegalArgumentException(
                    "Speed cannot be negative");
        }

        if (speedLimit < 0) {
            throw new IllegalArgumentException(
                    "Speed limit cannot be negative");
        }

        if (location == null
                || location.isBlank()) {
            throw new IllegalArgumentException(
                    "Location cannot be empty");
        }

        this.violationId = violationId;
        this.vehicleNumber =
                vehicleNumber.toUpperCase();
        this.violationType = violationType;
        this.severity = severity;
        this.speed = speed;
        this.speedLimit = speedLimit;
        this.location = location;
        this.timestamp = LocalDateTime.now();
    }

    public String getViolationId() {
        return violationId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public ViolationType getViolationType() {
        return violationType;
    }

    public ViolationSeverity getSeverity() {
        return severity;
    }

    public double getSpeed() {
        return speed;
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {

        return "Violation ID: "
                + violationId
                + " | Vehicle: "
                + vehicleNumber
                + " | Type: "
                + violationType
                + " | Severity: "
                + severity
                + " | Location: "
                + location;
    }
}
