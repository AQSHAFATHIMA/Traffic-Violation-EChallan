package com.example.traffic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TrafficViolationService {

    private final Map<String, Vehicle> vehicles =
            new LinkedHashMap<>();

    private final Map<String, Violation> violations =
            new LinkedHashMap<>();

    private final Map<String, Challan> challans =
            new LinkedHashMap<>();

    private static final double
            OVER_SPEEDING_BASE_FINE = 1000.0;

    private static final double
            SIGNAL_VIOLATION_BASE_FINE = 500.0;

    private static final double
            ILLEGAL_PARKING_BASE_FINE = 300.0;

    // =========================
    // VEHICLE REGISTRATION
    // =========================

    public void registerVehicle(
            Vehicle vehicle)
            throws TrafficViolationException {

        if (vehicle == null) {
            throw new TrafficViolationException(
                    "Vehicle cannot be null");
        }

        String number =
                vehicle.getVehicleNumber();

        if (vehicles.containsKey(number)) {
            throw new TrafficViolationException(
                    "Vehicle already registered: "
                            + number);
        }

        vehicles.put(number, vehicle);
    }

    public Vehicle getVehicle(
            String vehicleNumber)
            throws TrafficViolationException {

        Vehicle vehicle =
                vehicles.get(
                        normalize(vehicleNumber));

        if (vehicle == null) {
            throw new TrafficViolationException(
                    "Vehicle not found: "
                            + vehicleNumber);
        }

        return vehicle;
    }

    // =========================
    // RECORD VIOLATION
    // =========================

    public Violation recordViolation(
            Violation violation)
            throws TrafficViolationException {

        if (violation == null) {
            throw new TrafficViolationException(
                    "Violation cannot be null");
        }

        getVehicle(
                violation.getVehicleNumber());

        if (violations.containsKey(
                violation.getViolationId())) {

            throw new TrafficViolationException(
                    "Duplicate violation ID: "
                            + violation.getViolationId());
        }

        validateViolationRules(violation);

        violations.put(
                violation.getViolationId(),
                violation);

        return violation;
    }

    private void validateViolationRules(
            Violation violation)
            throws TrafficViolationException {

        if (violation.getViolationType()
                == ViolationType.OVER_SPEEDING) {

            if (violation.getSpeed()
                    <= violation.getSpeedLimit()) {

                throw new TrafficViolationException(
                        "Over-speeding violation requires "
                                + "speed greater than speed limit");
            }
        }
    }

    // =========================
    // FINE CALCULATION
    // =========================

    public double calculateFine(
            Violation violation) {

        double baseFine;

        switch (violation.getViolationType()) {

            case OVER_SPEEDING:
                baseFine =
                        OVER_SPEEDING_BASE_FINE;
                break;

            case SIGNAL_VIOLATION:
                baseFine =
                        SIGNAL_VIOLATION_BASE_FINE;
                break;

            case ILLEGAL_PARKING:
                baseFine =
                        ILLEGAL_PARKING_BASE_FINE;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported violation type");
        }

        double severityFine =
                baseFine
                        * violation
                        .getSeverity()
                        .getMultiplier();

        int previousViolations =
                countPreviousViolations(
                        violation.getVehicleNumber());

        /*
         * Repeated violation penalty:
         * 0 previous -> 100%
         * 1 previous  -> 150%
         * 2 previous  -> 200%
         * 3 previous  -> 250%
         */
        double repeatedPenaltyMultiplier =
                1.0
                        + (previousViolations
                        * 0.50);

        return severityFine
                * repeatedPenaltyMultiplier;
    }

    private int countPreviousViolations(
            String vehicleNumber) {

        int count = 0;

        for (Violation violation
                : violations.values()) {

            if (violation.getVehicleNumber()
                    .equalsIgnoreCase(
                            vehicleNumber)) {

                count++;
            }
        }

        return count;
    }

    // =========================
    // E-CHALLAN GENERATION
    // =========================

    public Challan generateChallan(
            String violationId)
            throws TrafficViolationException {

        Violation violation =
                violations.get(violationId);

        if (violation == null) {
            throw new TrafficViolationException(
                    "Violation not found: "
                            + violationId);
        }

        if (challanExistsForViolation(
                violationId)) {

            throw new TrafficViolationException(
                    "Duplicate challan is not allowed "
                            + "for violation: "
                            + violationId);
        }

        double fine =
                calculateFine(violation);

        String challanId =
                "CH-" + violationId;

        Challan challan =
                new Challan(
                        challanId,
                        violation.getVehicleNumber(),
                        violation.getViolationId(),
                        violation.getViolationType(),
                        fine);

        challans.put(
                challanId,
                challan);

        return challan;
    }

    private boolean challanExistsForViolation(
            String violationId) {

        for (Challan challan
                : challans.values()) {

            if (challan.getViolationId()
                    .equals(violationId)) {

                return true;
            }
        }

        return false;
    }

    // =========================
    // PAYMENT
    // =========================

    public void payChallan(
            String challanId)
            throws TrafficViolationException {

        Challan challan =
                challans.get(challanId);

        if (challan == null) {
            throw new TrafficViolationException(
                    "Challan not found: "
                            + challanId);
        }

        if (challan.isPaid()) {
            throw new TrafficViolationException(
                    "Challan is already paid: "
                            + challanId);
        }

        challan.markAsPaid();
    }

    // =========================
    // OUTSTANDING FINE
    // =========================

    public double calculateOutstandingFine(
            String vehicleNumber)
            throws TrafficViolationException {

        getVehicle(vehicleNumber);

        double total = 0;

        for (Challan challan
                : challans.values()) {

            if (challan.getVehicleNumber()
                    .equalsIgnoreCase(
                            vehicleNumber)
                    && !challan.isPaid()) {

                total += challan.getFineAmount();
            }
        }

        return total;
    }

    // =========================
    // VEHICLE CLASSIFICATION
    // =========================

    public String classifyVehicle(
            String vehicleNumber)
            throws TrafficViolationException {

        getVehicle(vehicleNumber);

        int count =
                countViolations(vehicleNumber);

        if (count == 0) {
            return "GOOD";
        }

        if (count <= 2) {
            return "WARNING";
        }

        if (count <= 4) {
            return "HIGH_RISK";
        }

        return "CRITICAL_RISK";
    }

    private int countViolations(
            String vehicleNumber) {

        int count = 0;

        for (Violation violation
                : violations.values()) {

            if (violation.getVehicleNumber()
                    .equalsIgnoreCase(
                            vehicleNumber)) {

                count++;
            }
        }

        return count;
    }

    // =========================
    // SEARCH / HISTORY
    // =========================

    public List<Violation>
    getViolationHistory(
            String vehicleNumber)
            throws TrafficViolationException {

        getVehicle(vehicleNumber);

        List<Violation> result =
                new ArrayList<>();

        for (Violation violation
                : violations.values()) {

            if (violation.getVehicleNumber()
                    .equalsIgnoreCase(
                            vehicleNumber)) {

                result.add(violation);
            }
        }

        return result;
    }

    public List<Challan>
    getChallansForVehicle(
            String vehicleNumber)
            throws TrafficViolationException {

        getVehicle(vehicleNumber);

        List<Challan> result =
                new ArrayList<>();

        for (Challan challan
                : challans.values()) {

            if (challan.getVehicleNumber()
                    .equalsIgnoreCase(
                            vehicleNumber)) {

                result.add(challan);
            }
        }

        return result;
    }

    public int getVehicleCount() {
        return vehicles.size();
    }

    public int getViolationCount() {
        return violations.size();
    }

    public int getChallanCount() {
        return challans.size();
    }

    public void displayVehicles() {

        System.out.println(
                "\n===== REGISTERED VEHICLES =====");

        for (Vehicle vehicle
                : vehicles.values()) {

            System.out.println(vehicle);
        }
    }

    public void displayChallans() {

        System.out.println(
                "\n===== E-CHALLANS =====");

        for (Challan challan
                : challans.values()) {

            System.out.println(challan);
        }
    }

    private String normalize(
            String vehicleNumber) {

        if (vehicleNumber == null) {
            return "";
        }

        return vehicleNumber
                .trim()
                .toUpperCase();
    }
}
