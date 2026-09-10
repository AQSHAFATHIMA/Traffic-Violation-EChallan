package com.example.traffic;

public class Main {

    public static void main(String[] args)
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        // =========================
        // REGISTER VEHICLES
        // =========================

        Vehicle vehicle1 =
                new Vehicle(
                        "TN01AB1234",
                        "Arun Kumar",
                        "9876543210",
                        VehicleType.CAR);

        Vehicle vehicle2 =
                new Vehicle(
                        "TN02CD5678",
                        "Rahul Kumar",
                        "9876543211",
                        VehicleType.BIKE);

        Vehicle vehicle3 =
                new Vehicle(
                        "TN03EF9012",
                        "Vijay Kumar",
                        "9876543212",
                        VehicleType.BUS);

        service.registerVehicle(vehicle1);
        service.registerVehicle(vehicle2);
        service.registerVehicle(vehicle3);

        service.displayVehicles();

        // =========================
        // OVER-SPEEDING
        // =========================

        Violation violation1 =
                new Violation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.OVER_SPEEDING,
                        ViolationSeverity.HIGH,
                        90,
                        60,
                        "Chennai Anna Salai");

        service.recordViolation(violation1);

        Challan challan1 =
                service.generateChallan("V001");

        System.out.println(
                "\nGenerated Challan:");
        System.out.println(challan1);

        // =========================
        // SIGNAL VIOLATION
        // =========================

        Violation violation2 =
                new Violation(
                        "V002",
                        "TN02CD5678",
                        ViolationType.SIGNAL_VIOLATION,
                        ViolationSeverity.MEDIUM,
                        0,
                        0,
                        "T Nagar Signal");

        service.recordViolation(violation2);

        Challan challan2 =
                service.generateChallan("V002");

        System.out.println(challan2);

        // =========================
        // PAYMENT
        // =========================

        service.payChallan(
                challan1.getChallanId());

        System.out.println(
                "\nAfter payment:");
        System.out.println(challan1);

        // =========================
        // OUTSTANDING FINE
        // =========================

        double outstanding =
                service.calculateOutstandingFine(
                        "TN02CD5678");

        System.out.println(
                "\nOutstanding fine for TN02CD5678: ₹"
                        + outstanding);

        // =========================
        // CLASSIFICATION
        // =========================

        System.out.println(
                "\nVehicle classification:");

        System.out.println(
                "TN01AB1234 -> "
                        + service.classifyVehicle(
                                "TN01AB1234"));

        System.out.println(
                "TN02CD5678 -> "
                        + service.classifyVehicle(
                                "TN02CD5678"));

        // =========================
        // DISPLAY CHALLANS
        // =========================

        service.displayChallans();

        // =========================
        // HISTORY
        // =========================

        System.out.println(
                "\nViolation history for TN01AB1234:");

        for (Violation violation
                : service.getViolationHistory(
                        "TN01AB1234")) {

            System.out.println(violation);
        }
    }
}
