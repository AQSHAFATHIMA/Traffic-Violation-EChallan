package com.example.traffic;

public enum ViolationSeverity {

    LOW(1.0),
    MEDIUM(1.5),
    HIGH(2.0),
    CRITICAL(3.0);

    private final double multiplier;

    ViolationSeverity(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
