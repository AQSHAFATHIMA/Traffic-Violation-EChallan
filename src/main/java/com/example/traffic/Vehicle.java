package com.example.traffic;

public class Vehicle {

    private final String vehicleNumber;
    private final String ownerName;
    private final String ownerPhone;
    private final VehicleType vehicleType;

    public Vehicle(
            String vehicleNumber,
            String ownerName,
            String ownerPhone,
            VehicleType vehicleType) {

        if (vehicleNumber == null
                || vehicleNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Vehicle number cannot be empty");
        }

        if (!vehicleNumber.matches(
                "[A-Za-z0-9-]{4,15}")) {
            throw new IllegalArgumentException(
                    "Invalid vehicle number");
        }

        if (ownerName == null
                || ownerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Owner name cannot be empty");
        }

        if (ownerPhone == null
                || !ownerPhone.matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "Owner phone must contain 10 digits");
        }

        if (vehicleType == null) {
            throw new IllegalArgumentException(
                    "Vehicle type is required");
        }

        this.vehicleNumber =
                vehicleNumber.toUpperCase();

        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.vehicleType = vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public String toString() {

        return "Vehicle Number: "
                + vehicleNumber
                + " | Owner: "
                + ownerName
                + " | Phone: "
                + ownerPhone
                + " | Type: "
                + vehicleType;
    }
}
